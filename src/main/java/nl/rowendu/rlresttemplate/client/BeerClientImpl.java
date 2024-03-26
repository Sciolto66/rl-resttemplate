package nl.rowendu.rlresttemplate.client;

import nl.rowendu.rlresttemplate.model.BeerDTO;
import org.springframework.data.domain.Page;

public class BeerClientImpl implements BeerClient {
    @Override
    public Page<BeerDTO> listBeers() {
        return null;
    }
}
