package db61b;
import static org.junit.Assert.*;
import org.junit.Test;

public class testTable {
    @Test
    public void TestAdd() {
        String[] colTitles = {"Company Name", "Product Description", "Position Description"};
        String[] comp1 = {"Rubrik", "data storage", "new grad role"};
        String[] comp2 = {"Apple", "apples", "apple cutter"};
        String[] comp3 = {"CapitalOne", "money", "money printers"};
        String[] comp4 = {"Apple", "apples", "apple cutter"};
        Table companyInfo = new Table(colTitles);
        assertTrue(companyInfo.add(comp1));
        assertTrue(companyInfo.add(comp2));
        assertTrue(companyInfo.add(comp3));
        assertFalse(companyInfo.add(comp4));
        companyInfo.print();
    }

}
