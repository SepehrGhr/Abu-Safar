package ir.ac.kntu.abusafar.document;

import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.BusClass;
import ir.ac.kntu.abusafar.util.constants.enums.FlightClass;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.InnerField;


import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Document(indexName = "tickets")
public class TicketDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long tripId;

    @Field(type = FieldType.Keyword)
    private AgeRange age;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    private TripType tripVehicle;

    @Field(type = FieldType.Date)
    private OffsetDateTime departureTimestamp;

    @Field(type = FieldType.Date)
    private OffsetDateTime arrivalTimestamp;

    @Field(type = FieldType.Short)
    private Short stopCount;

    @Field(type = FieldType.Integer)
    private Integer availableSeats;

    @Field(type = FieldType.Object)
    private Location origin;

    @Field(type = FieldType.Object)
    private Location destination;

    @Field(type = FieldType.Object)
    private Company company;

    @Field(type = FieldType.Object)
    private VehicleDetails vehicleDetails;


    @Getter
    @Setter
    public static class Location {
        @Field(type = FieldType.Keyword)
        private String city;
        @Field(type = FieldType.Keyword)
        private String province;
        @Field(type = FieldType.Keyword)
        private String country;
    }

    @Getter
    @Setter
    public static class Company {
        @MultiField(
                mainField = @Field(type = FieldType.Text),
                otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) }
        )
        private String name;

        @Field(type = FieldType.Keyword, index = false)
        private String logo;
    }

    @Getter
    @Setter
    public static class VehicleDetails {
        @Field(type = FieldType.Short)
        private Short trainStars;

        @Field(type = FieldType.Keyword)
        private String roomType;

        @Field(type = FieldType.Keyword)
        private BusClass busClass;

        @Field(type = FieldType.Keyword)
        private FlightClass flightClass;
    }
}