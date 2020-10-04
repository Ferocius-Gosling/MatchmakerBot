package test;


import com.company.AnswersStorage;
import com.company.DialogLogic;
import com.company.DialogStates;
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
    public void testRegAge(){
        var user = new User(1);
        user.changeCurrentState(DialogStates.REG_AGE);
        user.setReg(true);
        var response = logic.getResponse(user, "20");
        Assert.assertEquals(response, AnswersStorage.regCityMessage);
    }

    @Test
    public void testRegAgeIncorrect(){
        var user = new User(1);
        user.changeCurrentState(DialogStates.REG_AGE);
        user.setReg(true);
        var resp = logic.getResponse(user, "Мне двадцать лет");
        Assert.assertEquals(resp, AnswersStorage.wrongAgeMessage);
    }

    @Test
    public void testFullRegistration(){
        var user = new User(1);
        var reg = logic.getResponse(user, "/reg");
        var name = logic.getResponse(user, "John Smith");
        var age = logic.getResponse(user, "23");
        var city = logic.getResponse(user, "New York");
        var info = logic.getResponse(user, "Something about me");
        Assert.assertEquals(reg, AnswersStorage.registerNameMessage);
        Assert.assertEquals(name, AnswersStorage.regAgeMessage);
        Assert.assertEquals(age, AnswersStorage.regCityMessage);
        Assert.assertEquals(city, AnswersStorage.regInfoMessage);
        Assert.assertEquals(info, AnswersStorage.getUserInfo(user));
    }

    @Test
    public void testDefaultResponse(){
        var string = logic.getResponse(user0, "blablabla");
        Assert.assertEquals(string, AnswersStorage.defaultMessage);
    }
}
