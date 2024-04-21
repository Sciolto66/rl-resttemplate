package nl.rowendu.rlresttemplate.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import nl.rowendu.rlresttemplate.config.RestTemplateBuilderConfig;
import nl.rowendu.rlresttemplate.model.BeerDTO;
import nl.rowendu.rlresttemplate.model.BeerDTOPageImpl;
import nl.rowendu.rlresttemplate.model.BeerParameters;
import nl.rowendu.rlresttemplate.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

@RestClientTest
@Import(RestTemplateBuilderConfig.class)
class BeerClientMockTest {

  static final String URL = "http://localhost:8080";

  @Autowired ObjectMapper objectMapper;
  @Autowired RestTemplateBuilder restTemplateBuilderConfigured;
  BeerClient beerClient;
  MockRestServiceServer server;

  @Mock
  RestTemplateBuilder mockRestTemplateBuilder =
      new RestTemplateBuilder(new MockServerRestTemplateCustomizer());

  BeerDTO expectedBeer;
  String beerPayload;

  @BeforeEach
  void setUp() throws JsonProcessingException {
    RestTemplate restTemplate = restTemplateBuilderConfigured.build();
    server = MockRestServiceServer.bindTo(restTemplate).build();
    when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
    beerClient = new BeerClientImpl(mockRestTemplateBuilder);
    ((BeerClientImpl) beerClient).setBaseUrl(URL);
    expectedBeer = getBeerDto();
    beerPayload = objectMapper.writeValueAsString(expectedBeer);
  }

  @Test
  void testListBeersWithQueryParam() throws JsonProcessingException {
    String beerPayload = objectMapper.writeValueAsString(getPage());
    String beerName = "Mango Bobs";
    String encodedBeerName = UriUtils.encode(beerName, StandardCharsets.UTF_8);

    URI uri = UriComponentsBuilder.fromHttpUrl(URL + BeerClientImpl.GET_BEER_PATH)
            .queryParam("beerName", beerName)
            .build().encode().toUri();

    server.expect(method(HttpMethod.GET))
            .andExpect(requestTo(uri))
            .andExpect(queryParam("beerName", encodedBeerName))
            .andRespond(withSuccess(beerPayload, MediaType.APPLICATION_JSON));

    BeerParameters parameters = new BeerParameters();
    parameters.setBeerName(beerName);

    Page<BeerDTO> responsePage = beerClient.listBeers(parameters);
    assertThat(responsePage.getContent()).hasSize(1);
    assertThat(responsePage.getContent().get(0).getBeerName()).isEqualTo(beerName);

  }

  @Test
  void testDeleteNotFound() {
    server
        .expect(method(HttpMethod.DELETE))
        .andExpect(
            requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, expectedBeer.getId()))
        .andRespond(withResourceNotFound());

    UUID beerId = expectedBeer.getId();
    assertThrows(HttpClientErrorException.class, () -> beerClient.deleteBeer(beerId));

    server.verify();
  }

  @Test
  void testDeleteBeer() {
    server
        .expect(method(HttpMethod.DELETE))
        .andExpect(
            requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, expectedBeer.getId()))
        .andRespond(withNoContent());

    beerClient.deleteBeer(expectedBeer.getId());
    server.verify();
  }

  @Test
  void testUpdateBeer() {
    server
        .expect(method(HttpMethod.PUT))
        .andExpect(
            requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, expectedBeer.getId()))
        .andRespond(withNoContent());

    mockGetOperation();

    BeerDTO actualBeer = beerClient.updateBeer(expectedBeer);
    assertThat(actualBeer.getId()).isEqualTo(expectedBeer.getId());
  }

  @Test
  void testCreateBeer() {
    URI uri =
        UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH)
            .build(expectedBeer.getId());

    server
        .expect(method(HttpMethod.POST))
        .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
        .andRespond(withAccepted().location(uri));

    mockGetOperation();

    BeerDTO actualBeer = beerClient.createBeer(expectedBeer);
    assertThat(actualBeer.getId()).isEqualTo(expectedBeer.getId());
  }

  private void mockGetOperation() {
    server
        .expect(method(HttpMethod.GET))
        .andExpect(
            requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, expectedBeer.getId()))
        .andRespond(withSuccess(beerPayload, MediaType.APPLICATION_JSON));
  }

  @Test
  void testListBeers() throws JsonProcessingException {
    String beerPayload = objectMapper.writeValueAsString(getPage());
    server
        .expect(method(HttpMethod.GET))
        .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
        .andRespond(withSuccess(beerPayload, MediaType.APPLICATION_JSON));

    Page<BeerDTO> beerDTOS = beerClient.listBeers();
    assertThat(beerDTOS.getContent()).isNotEmpty();
  }

  @Test
  void testGetBeerById() {
    mockGetOperation();

    BeerDTO actualBeer = beerClient.getBeerById(expectedBeer.getId());
    assertThat(actualBeer.getId()).isEqualTo(expectedBeer.getId());
  }

  BeerDTOPageImpl<BeerDTO> getPage() {
    return new BeerDTOPageImpl<>(Collections.singletonList(getBeerDto()), 1, 25, 1);
  }

  BeerDTO getBeerDto() {
    return BeerDTO.builder()
        .id(UUID.randomUUID())
        .price(new BigDecimal("10.99"))
        .beerName("Mango Bobs")
        .beerStyle(BeerStyle.IPA)
        .quantityOnHand(500)
        .upc("123245")
        .build();
  }
}
