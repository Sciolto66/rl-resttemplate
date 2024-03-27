package nl.rowendu.rlresttemplate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeerParameters {
    private String beerName;
    private BeerStyle beerStyle;
    private boolean showInventory;
    private int pageNumber;
    private int pageSize;

}
