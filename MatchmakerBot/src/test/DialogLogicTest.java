package test;


import com.company.AnswersStorage;
import com.company.DialogLogic;
import org.junit.Assert;
import org.junit.Test;

public class DialogLogicTest {
    public DialogLogic logic = new DialogLogic();

    @Test
    public void testHelpResponse(){
        var string = logic.getResponse("/help");
        Assert.assertEquals(string, AnswersStorage.helpMessage);
    }

    @Test
    public void testRegResponse(){
        var string = logic.getResponse("/reg");
        Assert.assertEquals(string, AnswersStorage.registerMessage);
    }

    @Test
    public void testDefaultResponse(){
        var string = logic.getResponse("blablabla");
        Assert.assertEquals(string, AnswersStorage.defaultMessage);
    }
}
