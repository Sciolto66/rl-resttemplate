package nl.rowendu.rlresttemplate.client;

import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nl.rowendu.rlresttemplate.model.BeerDTO;
import nl.rowendu.rlresttemplate.model.BeerDTOPageImpl;
import nl.rowendu.rlresttemplate.model.BeerParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

  public static final String GET_BEER_PATH = "/api/v1/beer";
  public static final String GET_BEER_BY_ID_PATH = "/api/v1/beer/{beerId}";
  private static final Logger log = LoggerFactory.getLogger(BeerClientImpl.class);

  private final RestTemplateBuilder restTemplateBuilder;
  
  @Setter
  @Value("${rest.template.rootUrl}") private String baseUrl;

  @Override
  public void deleteBeer(UUID beerId) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    restTemplate.delete(GET_BEER_BY_ID_PATH, beerId);

  }

  @Override
  public BeerDTO updateBeer(BeerDTO updatedBeer) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    restTemplate.put(GET_BEER_BY_ID_PATH, updatedBeer, updatedBeer.getId());
    return getBeerById(updatedBeer.getId());
  }

  @Override
  public BeerDTO createBeer(BeerDTO newBeer) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    URI uri = restTemplate.postForLocation(GET_BEER_PATH, newBeer);
    if (uri == null) {
      throw new MissingLocationException("Failed to create beer, no location returned");
    }
    return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
  }

  @Override
  public BeerDTO getBeerById(UUID beerId) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    return restTemplate.getForObject(GET_BEER_BY_ID_PATH, BeerDTO.class, beerId);
  }

  @Override
  public Page<BeerDTO> listBeers(BeerParameters parameters) {
    RestTemplate restTemplate = restTemplateBuilder.build();

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromHttpUrl(baseUrl).path(GET_BEER_PATH);

    if (parameters.getBeerName() != null) {
      uriComponentsBuilder.queryParam("beerName", parameters.getBeerName());
    }
    if (parameters.getBeerStyle() != null) {
      uriComponentsBuilder.queryParam("beerStyle", parameters.getBeerStyle());
    }
    if (parameters.isShowInventory()) {
      uriComponentsBuilder.queryParam("showInventory", true);
    }
    if (parameters.getPageNumber() > 0) {
      uriComponentsBuilder.queryParam("pageNumber", parameters.getPageNumber());
    }
    if (parameters.getPageSize() > 0) {
      uriComponentsBuilder.queryParam("pageSize", parameters.getPageSize());
    }

    UriComponents uriComponents = uriComponentsBuilder.build();
    URI uri = uriComponents.toUri();

    try {
      ResponseEntity<BeerDTOPageImpl<BeerDTO>> response =
              restTemplate.exchange(
                      uri,
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