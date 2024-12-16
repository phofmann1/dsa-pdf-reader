//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2023.11.07 um 04:39:52 PM CET 
//


package de.pho.realmworks.analyser.exportxsd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the de.pho.realmworks.analyser.exportxsd package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

  private final static QName _Annotation_QNAME = new QName("urn:lonewolfdevel.com:realm-works-export", "annotation");
  private final static QName _Summary_QNAME = new QName("urn:lonewolfdevel.com:realm-works-export", "summary");
  private final static QName _GmDirections_QNAME = new QName("urn:lonewolfdevel.com:realm-works-export", "gm_directions");
  private final static QName _Purpose_QNAME = new QName("urn:lonewolfdevel.com:realm-works-export", "purpose");
  private final static QName _Description_QNAME = new QName("urn:lonewolfdevel.com:realm-works-export", "description");
  private final static QName _CoverArt_QNAME = new QName("urn:lonewolfdevel.com:realm-works-export", "cover_art");

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.pho.realmworks.analyser.exportxsd
   */
  public ObjectFactory() {
  }

  /**
   * Create an instance of {@link Snippet }
   */
  public Snippet createSnippet() {
    return new Snippet();
  }

  /**
   * Create an instance of {@link Contents }
   */
  public Contents createContents() {
    return new Contents();
  }

  /**
   * Create an instance of {@link Topic }
   */
  public Topic createTopic() {
    return new Topic();
  }

  /**
   * Create an instance of {@link Alias }
   */
  public Alias createAlias() {
    return new Alias();
  }

  /**
   * Create an instance of {@link Section }
   */
  public Section createSection() {
    return new Section();
  }

  /**
   * Create an instance of {@link TagAssign }
   */
  public TagAssign createTagAssign() {
    return new TagAssign();
  }

  /**
   * Create an instance of {@link Connection }
   */
  public Connection createConnection() {
    return new Connection();
  }

  /**
   * Create an instance of {@link Dconnection }
   */
  public Dconnection createDconnection() {
    return new Dconnection();
  }

  /**
   * Create an instance of {@link Link }
   */
  public Link createLink() {
    return new Link();
  }

  /**
   * Create an instance of {@link SpanInfo }
   */
  public SpanInfo createSpanInfo() {
    return new SpanInfo();
  }

  /**
   * Create an instance of {@link SpanList }
   */
  public SpanList createSpanList() {
    return new SpanList();
  }

  /**
   * Create an instance of {@link Span }
   */
  public Span createSpan() {
    return new Span();
  }

  /**
   * Create an instance of {@link Dlink }
   */
  public Dlink createDlink() {
    return new Dlink();
  }

  /**
   * Create an instance of {@link OtherSpans }
   */
  public OtherSpans createOtherSpans() {
    return new OtherSpans();
  }

  /**
   * Create an instance of {@link GameDate }
   */
  public GameDate createGameDate() {
    return new GameDate();
  }

  /**
   * Create an instance of {@link TagGlobal }
   */
  public TagGlobal createTagGlobal() {
    return new TagGlobal();
  }

  /**
   * Create an instance of {@link TextOverride }
   */
  public TextOverride createTextOverride() {
    return new TextOverride();
  }

  /**
   * Create an instance of {@link ContentSummary }
   */
  public ContentSummary createContentSummary() {
    return new ContentSummary();
  }

  /**
   * Create an instance of {@link Partition }
   */
  public Partition createPartition() {
    return new Partition();
  }

  /**
   * Create an instance of {@link Facet }
   */
  public Facet createFacet() {
    return new Facet();
  }

  /**
   * Create an instance of {@link Definition }
   */
  public Definition createDefinition() {
    return new Definition();
  }

  /**
   * Create an instance of {@link Details }
   */
  public Details createDetails() {
    return new Details();
  }

  /**
   * Create an instance of {@link Tag }
   */
  public Tag createTag() {
    return new Tag();
  }

  /**
   * Create an instance of {@link FacetGlobal }
   */
  public FacetGlobal createFacetGlobal() {
    return new FacetGlobal();
  }

  /**
   * Create an instance of {@link Export }
   */
  public Export createExport() {
    return new Export();
  }

  /**
   * Create an instance of {@link Structure }
   */
  public Structure createStructure() {
    return new Structure();
  }

  /**
   * Create an instance of {@link DomainGlobal }
   */
  public DomainGlobal createDomainGlobal() {
    return new DomainGlobal();
  }

  /**
   * Create an instance of {@link Overlay }
   */
  public Overlay createOverlay() {
    return new Overlay();
  }

  /**
   * Create an instance of {@link Domain }
   */
  public Domain createDomain() {
    return new Domain();
  }

  /**
   * Create an instance of {@link CategoryGlobal }
   */
  public CategoryGlobal createCategoryGlobal() {
    return new CategoryGlobal();
  }

  /**
   * Create an instance of {@link PartitionGlobal }
   */
  public PartitionGlobal createPartitionGlobal() {
    return new PartitionGlobal();
  }

  /**
   * Create an instance of {@link Category }
   */
  public Category createCategory() {
    return new Category();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "urn:lonewolfdevel.com:realm-works-export", name = "annotation")
  public JAXBElement<String> createAnnotation(String value) {
    return new JAXBElement<String>(_Annotation_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "urn:lonewolfdevel.com:realm-works-export", name = "summary")
  public JAXBElement<String> createSummary(String value) {
    return new JAXBElement<String>(_Summary_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "urn:lonewolfdevel.com:realm-works-export", name = "gm_directions")
  public JAXBElement<String> createGmDirections(String value) {
    return new JAXBElement<String>(_GmDirections_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "urn:lonewolfdevel.com:realm-works-export", name = "purpose")
  public JAXBElement<String> createPurpose(String value) {
    return new JAXBElement<String>(_Purpose_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "urn:lonewolfdevel.com:realm-works-export", name = "description")
  public JAXBElement<String> createDescription(String value) {
    return new JAXBElement<String>(_Description_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "urn:lonewolfdevel.com:realm-works-export", name = "cover_art")
  public JAXBElement<String> createCoverArt(String value) {
    return new JAXBElement<String>(_CoverArt_QNAME, String.class, null, value);
  }

}
