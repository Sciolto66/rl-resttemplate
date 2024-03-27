package nl.rowendu.rlresttemplate.client;

import lombok.RequiredArgsConstructor;
import nl.rowendu.rlresttemplate.model.BeerDTO;
import nl.rowendu.rlresttemplate.model.BeerDTOPageImpl;
import nl.rowendu.rlresttemplate.model.BeerParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

  private static final Logger log = LoggerFactory.getLogger(BeerClientImpl.class);
  private static final String GET_BEER_PATH = "/api/v1/beer";
  private final RestTemplateBuilder restTemplateBuilder;

  @Override
  public Page<BeerDTO> listBeers(BeerParameters parameters) {
    RestTemplate restTemplate = restTemplateBuilder.build();

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

    if (parameters.getBeerName() != null) {
      uriComponentsBuilder.queryParam("beerName", parameters.getBeerName());
    }

    if (parameters.getBeerStyle() != null) {
      uriComponentsBuilder.queryParam("beerStyle", parameters.getBeerStyle());
    }

    uriComponentsBuilder.queryParam("showInventory", parameters.isShowInventory());
    uriComponentsBuilder.queryParam("pageNumber", parameters.getPageNumber());
    if (parameters.getPageSize() > 0) {
      uriComponentsBuilder.queryParam("pageSize", parameters.getPageSize());
    }
    String url = uriComponentsBuilder.toUriString();

    log.info("Sending request to: {}", url);

    try {
      ResponseEntity<BeerDTOPageImpl<BeerDTO>> response =
              restTemplate.exchange(
                      url,
                      HttpMethod.GET,
                      null,
                      new ParameterizedTypeReference<>() {});

      return response.getBody();
    } catch (Exception e) {
      log.error("Error occurred: ", e);
      return null;
    }
  }

  @Override
  public Page<BeerDTO> listBeers() {
    BeerParameters parameters = new BeerParameters();
    parameters.setBeerName(null);
    parameters.setBeerStyle(null);
    parameters.setShowInventory(false);
    parameters.setPageNumber(0);
    parameters.setPageSize(0);

    return listBeers(parameters);
  }
}