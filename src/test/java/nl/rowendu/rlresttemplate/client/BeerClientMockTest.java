package nl.rowendu.rlresttemplate.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withAccepted;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;
import nl.rowendu.rlresttemplate.config.RestTemplateBuilderConfig;
import nl.rowendu.rlresttemplate.model.BeerDTO;
import nl.rowendu.rlresttemplate.model.BeerDTOPageImpl;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

  @BeforeEach
  void setUp() {
    RestTemplate restTemplate = restTemplateBuilderConfigured.build();
    server = MockRestServiceServer.bindTo(restTemplate).build();
    when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
    beerClient = new BeerClientImpl(mockRestTemplateBuilder);
  }

  @Test
  void testCreateBeer() throws JsonProcessingException {
    BeerDTO expectedBeer = getBeerDto();
    String payload = objectMapper.writeValueAsString(expectedBeer);
    URI uri =
        UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH)
            .build(expectedBeer.getId());

    server
        .expect(method(HttpMethod.POST))
        .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
        .andRespond(withAccepted().location(uri));

    server
        .expect(method(HttpMethod.GET))
        .andExpect(
            requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, expectedBeer.getId()))
        .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

    BeerDTO actualBeer = beerClient.createBeer(expectedBeer);
    assertThat(actualBeer.getId()).isEqualTo(expectedBeer.getId());
  }

  @Test
  void testListBeers() throws JsonProcessingException {
    String payload = objectMapper.writeValueAsString(getPage());

    server
        .expect(method(HttpMethod.GET))
        .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
        .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

    Page<BeerDTO> beerDTOS = beerClient.listBeers();
    assertThat(beerDTOS.getContent()).isNotEmpty();
  }

  @Test
  void testGetBeerById() throws JsonProcessingException {
    BeerDTO expectedBeer = getBeerDto();
    String payload = objectMapper.writeValueAsString(expectedBeer);

    server
        .expect(method(HttpMethod.GET))
        .andExpect(
            requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, expectedBeer.getId()))
        .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

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