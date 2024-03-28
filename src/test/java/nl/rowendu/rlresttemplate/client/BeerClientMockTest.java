package nl.rowendu.rlresttemplate.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
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

@RestClientTest
@Import(RestTemplateBuilderConfig.class)
class BeerClientMockTest {

  static final String URL = "http://localhost:8080";

  @Autowired ObjectMapper objectMapper;
  @Autowired RestTemplateBuilder restTemplateBuilderConfigured;
  BeerClient beerClient;
  MockRestServiceServer server;

  @Mock
  RestTemplateBuilder mockRestTemplateBuilder = new RestTemplateBuilder(new MockServerRestTemplateCustomizer());
  @BeforeEach
    void setUp() {
    RestTemplate restTemplate = restTemplateBuilderConfigured.build();
    server = MockRestServiceServer.bindTo(restTemplate).build();
    when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
    beerClient = new BeerClientImpl(mockRestTemplateBuilder);
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

  BeerDTOPageImpl getPage() {
    return new BeerDTOPageImpl(Arrays.asList(getBeerDto()), 1, 25, 1);
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
