package nl.rowendu.rlresttemplate.client;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import nl.rowendu.rlresttemplate.model.BeerDTO;
import nl.rowendu.rlresttemplate.model.BeerParameters;
import nl.rowendu.rlresttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
class BeerClientImplTest {

  @Autowired BeerClientImpl beerClient;

  @Test
  void testDeleteBeer() {
    BeerDTO newBeer =
        BeerDTO.builder()
            .price(new BigDecimal("12.99"))
            .beerName("Mango bobs 3")
            .beerStyle(BeerStyle.IPA)
            .quantityOnHand(200)
            .upc("123456789")
            .build();

    BeerDTO savedBeer = beerClient.createBeer(newBeer);

    beerClient.deleteBeer(savedBeer.getId());

    assertThrows(HttpClientErrorException.class, () -> {
      beerClient.getBeerById(savedBeer.getId());
    });
  }

  @Test
  void testUpdateBeer() {
    BeerDTO newBeer =
        BeerDTO.builder()
            .price(new BigDecimal("12.99"))
            .beerName("Mango bobs 2")
            .beerStyle(BeerStyle.IPA)
            .quantityOnHand(200)
            .upc("123456789")
            .build();

    BeerDTO savedBeer = beerClient.createBeer(newBeer);

    final String newBeerName = "Mango bobs 2 updated";
    savedBeer.setBeerName(newBeerName);
    BeerDTO updatedBeer = beerClient.updateBeer(savedBeer);

    assertEquals(newBeerName, updatedBeer.getBeerName());
  }

  @Test
  void testCreateBeer() {
    BeerDTO newBeer =
        BeerDTO.builder()
            .price(new BigDecimal("12.99"))
            .beerName("Mango bobs")
            .beerStyle(BeerStyle.IPA)
            .quantityOnHand(200)
            .upc("123456789")
            .build();

    BeerDTO savedBeer = beerClient.createBeer(newBeer);

    assertNotNull(savedBeer);
  }

  @Test
  void getBeerById() {
    Page<BeerDTO> beerDTOPage = beerClient.listBeers();
    BeerDTO beerDTO = beerDTOPage.getContent().get(0);
    BeerDTO beerById = beerClient.getBeerById(beerDTO.getId());

    assertNotNull(beerById);
  }

  @Test
  void listBeersNoBeerName() {
    BeerParameters parameters = new BeerParameters();
    parameters.setBeerName(null);
    parameters.setBeerStyle(null);
    parameters.setShowInventory(false);
    parameters.setPageNumber(0);
    parameters.setPageSize(0);

    beerClient.listBeers(parameters);
  }

  @Test
  void listBeers() {
    BeerParameters parameters = new BeerParameters();
    parameters.setBeerName("IPA");
    parameters.setBeerStyle(BeerStyle.IPA);
    parameters.setShowInventory(true);
    parameters.setPageNumber(1);
    parameters.setPageSize(10);

    beerClient.listBeers(parameters);
  }

  @Test
  void listBeersNoParams() {
    beerClient.listBeers();
  }
}
