package controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class DeckApiTest {

    private String deckId;
    private  List<String> drawnCardArr = new LinkedList<String>();
    String pileName = "pile1";


    @Before
    public void init(){
        // creating new deck for testing
        RestAssured.baseURI = System.getProperty("BaseUrl");

        RestAssured.basePath = "api/deck/new/";
        RequestSpecification request = RestAssured.given();

        Response response = request.param("jokers_enabled",true).get();
        JsonPath re = response.jsonPath();
        deckId = re.get("deck_id");


        // draw some cards out of the deck
        RestAssured.basePath = "api/deck/"+deckId+"/draw/";
        request = RestAssured.given();

        response = request.param("count",10).get();
        re = response.jsonPath();
        List<Map<String,String>> drawnCards = (List<Map<String,String>>) re.get("cards");


        for(Map<String,String> i:drawnCards){
            drawnCardArr.add(i.get("code"));
        }
        List<Object> cards = re.get("cards");


        // add some cards to pile
        RestAssured.basePath = "api/deck/"+deckId+"/pile/"+pileName+"/add/";

        request = RestAssured.given();

        String pileCard = "";
        for(int i=0;i<5;i++){
            pileCard+=drawnCardArr.get(i);
            if(i!=4){
                pileCard+=",";
            }
        }

        response = request.queryParam("cards",pileCard).get();



    }

    @Test
    public void createDeckTest(){
        RestAssured.basePath = "api/deck/new/";
        RequestSpecification request = RestAssured.given();

        Response response = request.param("jokers_enabled",true).get();
        JsonPath re = response.jsonPath();
        Assert.assertEquals(response.getStatusCode(),200);
        Assert.assertEquals(re.get("success"),true);
        Assert.assertEquals(re.get("remaining"),54);

    }

    @Test
    public void drawCardTest(){
        RestAssured.basePath = "api/deck/"+deckId+"/draw/";
        RequestSpecification request = RestAssured.given();

        Response response = request.param("count",3).get();
        JsonPath re = response.jsonPath();
        List<Object> cards = re.get("cards");

        Assert.assertEquals(response.getStatusCode(),200);
        Assert.assertEquals(cards.size(),3);
    }

    @Test
    public void shuffleCardTest(){
        RestAssured.basePath = "api/deck/new/shuffle/";
        RequestSpecification request = RestAssured.given();

        Response response = request.param("deck_count",6).get();
        JsonPath re = response.jsonPath();

        Assert.assertEquals(re.get("remaining"),52*6);
        Assert.assertEquals(re.get("shuffled"),true);
    }

    @Test
    public void shuffleACardTest(){

        RestAssured.basePath = "api/deck/"+deckId+"/shuffle/";
        RequestSpecification request = RestAssured.given();

        Response response = request.get();
        JsonPath re = response.jsonPath();

        Assert.assertEquals(re.get("shuffled"),true);
    }

    @Test
    public void createPartialCardTest(){

        RestAssured.basePath = "api/deck/new/shuffle/";
        String cards = "AS,2S,KS,AD,2D,KD,AC,2C,KC,AH,2H,KH";

        RequestSpecification request = RestAssured.given();

        Response response = request.queryParam("cards",cards).get();
        JsonPath re = response.jsonPath();

        Assert.assertEquals(re.get("remaining"),12);
    }


    @Test
    public void addToPileTest(){

        String pileCard = "";
        for(int i=0;i<5;i++){
            pileCard+=drawnCardArr.get(i);
            if(i!=4){
                pileCard+=",";
            }
        }


        RestAssured.basePath = "api/deck/"+deckId+"/pile/"+pileName+"/add/";

        RequestSpecification request = RestAssured.given();

        Response response = request.queryParam("cards",pileCard).get();
        JsonPath re = response.jsonPath();

        Map<String,Map> piles = re.get("piles");
        Assert.assertEquals(piles.get("pile1").get("remaining"),5);
    }

    @Test
    public void shufflePileTest(){

        RestAssured.basePath = "api/deck/"+deckId+"/pile/"+pileName+"/shuffle/";

        RequestSpecification request = RestAssured.given();

        Response response = request.get();
        JsonPath re = response.jsonPath();

        Map<String,Map> piles = re.get("piles");
        Assert.assertEquals(piles.get("pile1").get("remaining"),5);
    }

    @Test
    public void listCardsInPileTest(){

        RestAssured.basePath = "api/deck/"+deckId+"/pile/"+pileName+"/list/";

        RequestSpecification request = RestAssured.given();

        Response response = request.get();
        JsonPath re = response.jsonPath();

        Map<String,Map> piles = re.get("piles");
        Assert.assertEquals(piles.get(pileName).get("remaining"),5);
    }

    // draw certain card
    @Test
    public void drawCardsInPileTest(){

        RestAssured.basePath = "api/deck/"+deckId+"/pile/"+pileName+"/draw/";

        RequestSpecification request = RestAssured.given();

        Response response = request.queryParam("cards",drawnCardArr.get(0)).get();
        JsonPath re = response.jsonPath();

        List<Map> cards = re.get("cards");
        Assert.assertEquals(cards.get(0).get("code"),drawnCardArr.get(0));
    }

    // draw top 2 cards
    @Test
    public void drawCardsInTopTest(){

        RestAssured.basePath = "api/deck/"+deckId+"/pile/"+pileName+"/draw/";

        RequestSpecification request = RestAssured.given();

        Response response = request.queryParam("count",2).get();
        JsonPath re = response.jsonPath();

        List<Map> cards = re.get("cards");
        for(int i=0;i<cards.size();i++){
            Assert.assertEquals(cards.get(i).get("code"),drawnCardArr.get(i+3));
        }
    }


    // draw bottom 2 cards
    @Test
    public void drawCardsAtBotTest() {

        RestAssured.basePath = "api/deck/"+deckId+"/pile/"+pileName+"/draw/bottom";

        RequestSpecification request = RestAssured.given();

        Response response = request.queryParam("count",2).get();
        JsonPath re = response.jsonPath();

        List<Map> cards = re.get("cards");
        for(int i=0;i<cards.size();i++){
            Assert.assertEquals(cards.get(i).get("code"),drawnCardArr.get(i));
        }
    }

}
