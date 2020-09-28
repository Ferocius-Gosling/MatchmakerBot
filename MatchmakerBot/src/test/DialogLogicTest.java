package test;


import com.company.AnswersStorage;
import com.company.DialogLogic;
import com.company.User;
import org.junit.Assert;
import org.junit.Test;

public class DialogLogicTest {
    public DialogLogic logic = new DialogLogic();
    public User user0 = new User(0);

    @Test
    public void testHelpResponse(){
        var string = logic.getResponse(user0,"/help");
        Assert.assertEquals(string, AnswersStorage.helpMessage);
    }

    @Test
    public void testRegResponse(){
        var string = logic.getResponse(user0, "/reg");
        Assert.assertEquals(string, AnswersStorage.registerNameMessage);
    }

    @Test
    public void testDefaultResponse(){
        var string = logic.getResponse(user0, "blablabla");
        Assert.assertEquals(string, AnswersStorage.defaultMessage);
    }
}
