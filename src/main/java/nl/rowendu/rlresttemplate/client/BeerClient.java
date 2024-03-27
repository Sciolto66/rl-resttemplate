package nl.rowendu.rlresttemplate.client;

import nl.rowendu.rlresttemplate.model.BeerDTO;
import nl.rowendu.rlresttemplate.model.BeerParameters;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface BeerClient {

    Page<BeerDTO> listBeers(BeerParameters parameters);
    Page<BeerDTO> listBeers();

    BeerDTO getBeerById(UUID beerId);
}
