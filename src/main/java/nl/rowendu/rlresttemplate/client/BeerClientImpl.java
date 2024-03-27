package nl.rowendu.rlresttemplate.client;

import lombok.RequiredArgsConstructor;
import nl.rowendu.rlresttemplate.model.BeerDTO;
import nl.rowendu.rlresttemplate.model.BeerDTOPageImpl;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

  private final RestTemplateBuilder restTemplateBuilder;

  private static final String GET_BEER_PATH = "/api/v1/beer";

  @Override
  public Page<BeerDTO> listBeers() {
    RestTemplate restTemplate = restTemplateBuilder.build();

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

    ResponseEntity<BeerDTOPageImpl> response =
        restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

    return response.getBody();
  }
}
