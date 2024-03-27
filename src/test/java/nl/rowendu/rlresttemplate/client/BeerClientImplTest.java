package nl.rowendu.rlresttemplate.client;

import nl.rowendu.rlresttemplate.model.BeerParameters;
import nl.rowendu.rlresttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BeerClientImplTest {

  @Autowired
  BeerClientImpl beerClient;

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