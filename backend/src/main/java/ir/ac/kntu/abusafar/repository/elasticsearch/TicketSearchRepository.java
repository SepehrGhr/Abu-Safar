package ir.ac.kntu.abusafar.repository.elasticsearch;

import ir.ac.kntu.abusafar.document.TicketDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TicketSearchRepository extends ElasticsearchRepository<TicketDocument, String> {
}