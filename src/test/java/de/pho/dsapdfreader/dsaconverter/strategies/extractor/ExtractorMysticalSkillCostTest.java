package de.pho.dsapdfreader.dsaconverter.strategies.extractor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.pho.dsapdfreader.dsaconverter.model.MysticalSkillRaw;
import de.pho.dsapdfreader.exporter.model.Cost;
import de.pho.dsapdfreader.exporter.model.enums.Unit;

class ExtractorMysticalSkillCostTest
{

  @BeforeEach
  void setUp()
  {
  }

  @AfterEach
  void tearDown()
  {
  }

  //Balsam Salabunde: 1 AsP pro LeP, mindestens jedoch 4 AsP (Kosten nicht modifizierbar)
  @Test
  void givenCostMinWithPlusCostPerUnit_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Balsam Salabunde", "1 AsP pro LeP, mindestens jedoch 4 AsP (Kosten nicht modifizierbar)");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.costMin = 4;
    expected.plusCost = 1;
    expected.plusCostPerMultiplier = 1;
    expected.plusCostUnit = Unit.LEP;
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  //Affenruf: 16 AsP
  @Test
  void givenBaseCost_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Affenruf", "16 AsP");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.cost = 16;
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  //"Dunkelheit", "16 AsP (Aktivierung des Zaubers) + 8 AsP pro 5 Minuten"
  @Test
  void givenBaseCostAndCostPerUnit_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Dunkelheit", "16 AsP (Aktivierung des Zaubers) + 8 AsP pro 5 Minuten");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.cost = 16;
    expected.plusCost = 8;
    expected.plusCostPerMultiplier = 5;
    expected.plusCostUnit = Unit.MINUTE;
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }


  // Destructibo: "16 AsP + so viele eigene permanente AsP, wie im Zielobjekt gespeichert sind (Kosten nicht modifizierbar)
  @Test
  void givenBaseCostAndPermanentCostSpecial_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Destructibo", "16 AsP + so viele eigene permanente AsP, wie im Zielobjekt gespeichert sind (Kosten nicht modifizierbar)");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.cost = 16;
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  // Schuppenhaut: 2 AsP (Aktivierung des Zaubers) + 1 AsP pro KR
  @Test
  void givenBaseCostAndPlusCost_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Schuppenhaut", "2 AsP (Aktivierung des Zaubers) + 1 AsP pro KR");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.cost = 2;
    expected.plusCost = 1;
    expected.plusCostPerMultiplier = 1;
    expected.plusCostUnit = Unit.COMBAT_ROUND;
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  // Hartes schmelze: 2/4 AsP für einen Gegenstand von der Größe einer Tasse/Truhe (Kosten nicht modifizierbar)
  @Test
  void givenBaseCostList_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Hartes schmelze", "2/4 AsP für einen Gegenstand von der Größe einer Tasse/Truhe (Kosten nicht modifizierbar)");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.costList = List.of(2, 4);
    expected.costListValues = List.of("Tasse", "Truhe");
    expected.costListUnit = Unit.SIZE;
    expected.costText = msr.cost;

    assertThat(result, samePropertyValuesAs(expected));
  }


  //Dämonenpakt beenden: 32 AsP, davon 8 permanent
  @Test
  void givenBaseCostAndPermCost_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Dämonenpakt beenden", "32 AsP, davon 8 permanent");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.cost = 32;
    expected.permanentCost = 8;
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  // Armatrutz: 4 AsP für RS 1, 8 AsP für RS 2, 16 AsP für RS 3 (Kosten nicht modifizierbar)
  @Test
  void givenCostListArmor_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Armatrutz", "4 AsP für RS 1, 8 AsP für RS 2, 16 AsP für RS 3 (Kosten nicht modifizierbar)");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.costList = List.of(4, 8, 16);
    expected.costListUnit = Unit.RS;
    expected.costListValues = List.of("1", "2", "3");
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  // "Duplicatus", "4 AsP pro DoppelgÃ¤nger (bei Misslingen entsprechend 2 AsP)"
  @Test
  void givenCostPerDuplicate_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Duplicatus", "4 AsP pro Doppelgänger (bei Misslingen entsprechend 2 AsP)");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.plusCost = 4;
    expected.plusCostPerMultiplier = 1;
    expected.plusCostUnit = Unit.DUPLICATE;

    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  // "Staub wandle", "4/8/16/32/64 AsP für die Größenkategorie winzig / klein / mittel / groß / riesig , davon 1/2/4/8/16 AsP permanent (Kosten nicht modifizierbar)"
  @Test
  void givenCostListAndPermanentList_I_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Staub wandle", "4/8/16/32/64 AsP für die Größenkategorie winzig / klein / mittel / groß / riesig , davon 1/2/4/8/16 AsP permanent (Kosten nicht modifizierbar)"
    );

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.costList = List.of(4, 8, 16, 32, 64);
    expected.costListValues = List.of("winzig", "klein", "mittel", "groß", "riesig");
    expected.costListUnit = Unit.SIZE;
    expected.permanentCostList = List.of(1, 2, 4, 8, 16);
    expected.costText = msr.cost;

    assertThat(result, samePropertyValuesAs(expected));
  }

  //"Totes handle", "4/8/16/32/64 AsP für die Größenkategorie winzig/klein/mittel/groß/riesig, davon 0/1/2/4/8 AsP permanent (Kosten nicht modifizierbar)"
  @Test
  void givenCostListAndPermanentList_II_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Totes handle", "4/8/16/32/64 AsP für die Größenkategorie winzig/klein/mittel/groß/riesig, davon 0/1/2/4/8 AsP permanent (Kosten nicht modifizierbar)");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.costList = List.of(4, 8, 16, 32, 64);
    expected.costListValues = List.of("winzig", "klein", "mittel", "groß", "riesig");
    expected.costListUnit = Unit.SIZE;
    expected.permanentCostList = List.of(0, 1, 2, 4, 8);
    expected.costText = msr.cost;

    assertThat(result, samePropertyValuesAs(expected));
  }

  //Immortalis Lebenszeit: 64 AsP, davon 16 AsP permanent (Kosten nicht modifizierbar) soll Techniken geben, mit denen man die Kosten des Zaubers auf andere Wesen Ã¼bertragen kann, aber dieses Wissen ist nicht in der Kenntnis des Rituals selbst enthalten.
  @Test
  void givenBaseCostAndPermanentCostAndPermanentCostSpecial_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Immortalis Lebenszeit", "64 AsP, davon 16 AsP permanent (Kosten nicht modifizierbar) soll Techniken geben, mit denen man die Kosten des Zaubers auf andere Wesen übertragen kann, aber dieses Wissen ist nicht in der Kenntnis des Rituals selbst enthalten.");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.cost = 64;
    expected.permanentCost = 16;
    // expected.permanentCostSpecial = "soll Techniken geben, mit denen man die Kosten des Zaubers auf andere Wesen übertragen kann, aber dieses Wissen ist nicht in der Kenntnis des Rituals selbst enthalten.";

    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  // Infinitum Immerdar, 64 AsP, davon 8 AsP permanent + Kosten des Zaubers, der in dem Objekt wirken soll
  @Test
  void givenCostAndPermanentCostAndPlusCost_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Infinitum Immerdar", "64 AsP, davon 8 AsP permanent + Kosten des Zaubers, der in dem Objekt wirken soll");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.cost = 64;
    expected.permanentCost = 8;
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  // Applicatus: 8 AsP + Kosten des fixierten Zaubers
  @Test
  void givenBaseCostAndPlusCostSpecial_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Applicatus", "8 AsP + Kosten des fixierten Zaubers");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.cost = 8;
    expected.costText = msr.cost;

    assertThat(result, samePropertyValuesAs(expected));
  }

  //"Dämonenbann", "8 AsP bzw. 16 AsP für Zauber mit Zielkategorie Zone"
  @Test
  void givenBaseCostListForAntimagic_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Dämonenbann", "8 AsP bzw. 16 AsP für Zauber mit Zielkategorie Zone");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.costList = List.of(8, 16);
    expected.costListValues = List.of("", "Zauber mit der Zielkategorie Zone");

    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  //Transformatio: 8/16/32/64 AsP (Aktivierung des Zaubers) + 4/8/16/32 AsP pro 5 Minuten für einen Gegenstand von der Größe einer Tasse/Truhe/Tür/Burgtor
  @Test
  @Disabled
  void givenBaseCostListAndPlusCostPerTimeAndPlusCostList_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Transformatio", "8/16/32/64 AsP (Aktivierung des Zaubers) + 4/8/16/32 AsP pro 5 Minuten für einen Gegenstand von der Größe einer Tasse/Truhe/Tür/Burgtor");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.costList = List.of(8, 16, 32, 64);
    expected.costListUnit = Unit.SIZE;
    expected.costListValues = List.of("Tasse", "Truhe", "Tür", "Burgtor");
    expected.plusCostPerMultiplier = 5;
    expected.plusCostUnit = Unit.MINUTE;
    expected.plusCostList = List.of(4, 8, 16, 32);
    expected.plusCostListUnit = Unit.SIZE;
    expected.plusCostListValues = List.of("Tasse", "Truhe", "Tür", "Burgtor");
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  // Zauberklinge Geisterspeer: abhängig von der Waffe (siehe unten) (Kosten nicht modifizierbar)
  @Test
  void givenBaseCostSpecial_I_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Zauberklinge Geisterspeer", "abhängig von der Waffe (siehe unten) (Kosten nicht modifizierbar)");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  // "Herr über das Tierreich", "KL (t) des Tieres in AsP (Kosten nicht modifizierbar)"
  @Test
  void givenBaseCostSpecial_II_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Herr über das Tierreich", "KL (t) des Tieres in AsP (Kosten nicht modifizierbar)");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }


  // Motoricus: mindestens 4 AsP (Aktivierung) + Hälfte der notwendigen AsP pro 5 Minuten (Kosten nicht modifizierbar) bedeutet, dass ein Zauberer bis zu 20 Stein bewegen kann, ohne dass der Zauberspruch teurer wird. Mit der Hälfte der notwendigen AsP ist gemeint, dass diese Kosten variieren, je nachdem wie schwer und dementsprechend teuer das bewegte Objekt ist.
  @Test
  @Disabled
  void given_whenRetrieveSkillCost_thenReturnCostWithGivenValues()
  {
    //given
    MysticalSkillRaw msr = initMysticalSkillRaw("Motoricus", "mindestens 4 AsP (Aktivierung) + Hälfte der notwendigen AsP pro 5 Minuten (Kosten nicht modifizierbar) bedeutet, dass ein Zauberer bis zu 20 Stein bewegen kann, ohne dass der Zauberspruch teurer wird. Mit der Hälfte der notwendigen AsP ist gemeint, dass diese Kosten variieren, je nachdem wie schwer und dementsprechend teuer das bewegte Objekt ist.");

    //when
    Cost result = ExtractorMysticalSkillCost.retrieveMysticalSkillCost(msr);

    //that
    Cost expected = new Cost();
    expected.costMin = 4;
    expected.plusCostPerMultiplier = 5;
    expected.plusCostUnit = Unit.MINUTE;
    expected.plusCostHalfBase = true;
    expected.costText = msr.cost;
    assertThat(result, samePropertyValuesAs(expected));
  }

  private MysticalSkillRaw initMysticalSkillRaw(String name, String cost)
  {
    MysticalSkillRaw msr = new MysticalSkillRaw();
    msr.name = name;
    msr.cost = cost;
    return msr;
  }
}