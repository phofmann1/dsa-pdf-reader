package de.pho.dsapdfreader.dsaconverter;


import java.util.concurrent.atomic.AtomicInteger;

import de.pho.dsapdfreader.config.TopicConfiguration;
import de.pho.dsapdfreader.pdf.model.TextWithMetaInfo;

public class DsaConverterMysticalSkillGrimoriumTricks extends DsaConverterMysticalSkillGrimorium
{
  @Override
  protected boolean checkIsNewSpell(AtomicInteger lastPage, TextWithMetaInfo currentPage, TopicConfiguration conf)
  {
    return currentPage.size == conf.nameSize;
  }

}
