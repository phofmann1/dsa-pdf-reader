package de.pho.dsapdfreader.exporter.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class MysticalSkillFeatureTest
{

    @Test
    void toValue()
    {
        assertEquals(18, MysticalSkillFeature.GOOD_COMBAT.toValue());
    }

    @Test
    void fromStringValid()
    {
        MysticalSkillFeature result = MysticalSkillFeature.fromString("Guter KamPf");
        assertEquals(MysticalSkillFeature.GOOD_COMBAT, result);
    }

    @Test
    void fromStringInvalid()
    {
        MysticalSkillFeature result = MysticalSkillFeature.fromString("Wurstbrot");
        assertNull(result);
    }

}