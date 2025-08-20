package ir.ac.kntu.abusafar.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import ir.ac.kntu.abusafar.document.TicketDocument;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultDetailsDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSearchRequestDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSelectRequestDTO;
import ir.ac.kntu.abusafar.dto.vehicle.BusDetailsDTO;
import ir.ac.kntu.abusafar.dto.vehicle.FlightDetailsDTO;
import ir.ac.kntu.abusafar.dto.vehicle.TrainDetailsDTO;
import ir.ac.kntu.abusafar.dto.vehicle.VehicleDetailsDTO;
import ir.ac.kntu.abusafar.repository.elasticsearch.TicketSearchRepository;
import ir.ac.kntu.abusafar.service.TicketSearchService;
import ir.ac.kntu.abusafar.util.constants.enums.BusClass;
import ir.ac.kntu.abusafar.util.constants.enums.FlightClass;
import ir.ac.kntu.abusafar.util.constants.enums.TrainRoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketSearchServiceImpl implements TicketSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final TicketSearchRepository ticketSearchRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(TicketSearchServiceImpl.class);

    @Autowired
    public TicketSearchServiceImpl(
            ElasticsearchOperations elasticsearchOperations,
            TicketSearchRepository ticketSearchRepository) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.ticketSearchRepository = ticketSearchRepository;
    }

    @Override
    public List<TicketResultItemDTO> searchTickets(TicketSearchRequestDTO request) {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        boolQueryBuilder.filter(q -> q.term(t -> t.field("origin.id").value(request.getOriginId())));
        boolQueryBuilder.filter(q -> q.term(t -> t.field("destination.id").value(request.getDestinationId())));

        boolQueryBuilder.filter(q -> q.term(t -> t.field("tripVehicle").value(request.getTripVehicle().name())));
        boolQueryBuilder.filter(q -> q.range(r -> r.field("availableSeats").gt(JsonData.of(0))));
        if (request.getAgeCategory() != null) {
            boolQueryBuilder.filter(q -> q.term(t -> t.field("age").value(request.getAgeCategory().name())));
        }

        if (request.getDepartureDate() != null) {
            LocalDate date = request.getDepartureDate();
            LocalTime time = request.getDepartureTime() != null ? request.getDepartureTime() : LocalTime.MIDNIGHT;

            long from = date.atTime(time).toInstant(ZoneOffset.UTC).toEpochMilli();
            long to = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();

            boolQueryBuilder.filter(q -> q.range(r -> r
                    .field("departureTimestamp")
                    .gte(JsonData.of(from))
                    .lt(JsonData.of(to))
            ));
        }

        if (request.getVehicleCompany() != null && !request.getVehicleCompany().isBlank()) {
            boolQueryBuilder.must(q -> q.match(m -> m.field("company.name").query(request.getVehicleCompany())));
        }

        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            boolQueryBuilder.filter(q -> q.range(r -> {
                r.field("price");
                if (request.getMinPrice() != null) r.gte(JsonData.of(request.getMinPrice().doubleValue()));
                if (request.getMaxPrice() != null) r.lte(JsonData.of(request.getMaxPrice().doubleValue()));
                return r;
            }));
        }

        if (request.getBusClass() != null && !request.getBusClass().isEmpty()) {
            // Convert the list of enums to a list of strings for the query
            List<FieldValue> busClassValues = request.getBusClass().stream()
                    .map(BusClass::name)
                    .map(FieldValue::of)
                    .collect(Collectors.toList());
            // Use a 'terms' query to match any of the values in the list
            boolQueryBuilder.filter(q -> q.terms(t -> t
                    .field("vehicleDetails.busClass")
                    .terms(ts -> ts.value(busClassValues))
            ));
        }

        if (request.getFlightClass() != null && !request.getFlightClass().isEmpty()) {
            // Convert the list of enums to a list of strings for the query
            List<FieldValue> flightClassValues = request.getFlightClass().stream()
                    .map(FlightClass::name)
                    .map(FieldValue::of)
                    .collect(Collectors.toList());
            // Use a 'terms' query to match any of the values in the list
            boolQueryBuilder.filter(q -> q.terms(t -> t
                    .field("vehicleDetails.flightClass")
                    .terms(ts -> ts.value(flightClassValues))
            ));
        }


        if (request.getTrainStars() != null) {
            boolQueryBuilder.filter(q -> q.range(r -> r.field("vehicleDetails.trainStars").gte(JsonData.of(request.getTrainStars()))));
        }

        Query finalQuery = new Query.Builder().bool(boolQueryBuilder.build()).build();
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(finalQuery).build();

        SearchHits<TicketDocument> searchHits = elasticsearchOperations.search(nativeQuery, TicketDocument.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    TicketDocument doc = hit.getContent();
                    return new TicketResultItemDTO(
                            doc.getTripId(),
                            doc.getAge(),
                            doc.getOrigin().getCity(),
                            doc.getDestination().getCity(),
                            doc.getDepartureTimestamp(),
                            doc.getArrivalTimestamp(),
                            doc.getTripVehicle(),
                            doc.getPrice(),
                            doc.getCompany().getName()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TicketResultDetailsDTO> selectTicket(TicketSelectRequestDTO requestDTO) {
        String documentId = requestDTO.getTripId() + "_" + requestDTO.getAgeCategory().name();
        Optional<TicketDocument> documentOpt = ticketSearchRepository.findById(documentId);
        return documentOpt.map(this::mapDocumentToDetailsDTO);
    }

    private TicketResultDetailsDTO mapDocumentToDetailsDTO(TicketDocument doc) {
        VehicleDetailsDTO vehicleDetailsDTO = null;
        TicketDocument.VehicleDetails details = doc.getVehicleDetails();

        if (details != null) {
            switch (doc.getTripVehicle()) {
                case TRAIN:
                    vehicleDetailsDTO = new TrainDetailsDTO(
                            details.getTrainStars(),
                            details.getRoomType() != null ? TrainRoomType.fromString(details.getRoomType()) : null
                    );
                    break;
                case BUS:
                    vehicleDetailsDTO = new BusDetailsDTO(
                            details.getBusClass(),
                            details.getChairType()
                    );
                    break;
                case FLIGHT:
                    vehicleDetailsDTO = new FlightDetailsDTO(
                            details.getFlightClass(),
                            details.getDepartureAirport(),
                            details.getArrivalAirport()
                    );
                    break;
            }
        }

        return new TicketResultDetailsDTO(
                doc.getOrigin() != null ? doc.getOrigin().getCity() : null,
                doc.getDestination() != null ? doc.getDestination().getCity() : null,
                doc.getDepartureTimestamp(),
                doc.getArrivalTimestamp(),
                doc.getTripVehicle(),
                doc.getPrice(),
                doc.getCompany() != null ? doc.getCompany().getName() : null,
                vehicleDetailsDTO,
                doc.getStopCount(),
                doc.getTotalCapacity(),
                doc.getReservedCapacity(),
                doc.getAge(),
                doc.getServices()
        );
    }
}