# DeckApi Test
### Idea
Use rest assure package to automate api testing.

I test the new deck and draw cards apis first. This two apis
is used in the init method which runs before every test.

Before every test, I create a new deck and draw some cards from the
deck. Then I add some drawn cards to a pile. All these operations make
all the other apis' testing convenient.

For each api, I use the rest assure functionality to do the http request 
and then check the correctness of the response based on my request url and 
parameters. 

##How to run:
 * command run:
 
   mvn compile test -DBaseUrl="https://deckofcardsapi.com/"

 * Using IDE:
 1. Environment property configuration
VM option for DeckApiTest class:
-ea -DBaseUrl=https://deckofcardsapi.com/

 2. Right click init() method, choose run "DeckApiTest".










 
 