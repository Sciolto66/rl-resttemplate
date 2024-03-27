package nl.rowendu.rlresttemplate.client;

import nl.rowendu.rlresttemplate.model.BeerDTO;
import nl.rowendu.rlresttemplate.model.BeerParameters;
import nl.rowendu.rlresttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BeerClientImplTest {

  @Autowired
  BeerClientImpl beerClient;

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