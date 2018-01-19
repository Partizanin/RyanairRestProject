package SpringMVC;

import SpringMVC.controller.InterconnectionsController;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InterconnectionsControllerTest {

    @Test
    public void getValidDate() {
        /*Format 2018-02-21T12:00*/
        /*yyyy-MM-dd'T'HH:mm*/

        InterconnectionsController interconnectionsController = new InterconnectionsController();
        String validDate = interconnectionsController.getValidDate(2);
        String actual = "2018-01-18T20:05";

        assertEquals(validDate, actual);
    }
}