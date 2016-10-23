package com.researchspace.dataverse.entities.facade;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.researchspace.dataverse.entities.Citation;
import com.researchspace.dataverse.entities.CitationField;
import com.researchspace.dataverse.entities.CitationType;
import com.researchspace.dataverse.entities.DataSetMetadataBlock;
import com.researchspace.dataverse.entities.Dataset;
import com.researchspace.dataverse.entities.DatasetVersion;

/**
 * Converts POJO Java facade into underlying JSON object representation
 * 
 * @author rspace
 *
 */
public class DatasetBuilder {

	private static final String PRODUCER_LOGO_URL = "producerLogoURL";
	private static final String PRODUCER_URL = "producerURL";
	private static final String PRODUCER_AFFILIATION = "producerAffiliation";
	private static final String PRODUCER_ABBREVIATION = "producerAbbreviation";
	private static final String PRODUCER_NAME = "producerName";
	private static final String PUBLICATION_URL = "publicationURL";
	private static final String PUBLICATION_ID = "publicationIDNumber";
	private static final String PUBLICATION_ID_TYPE = "publicationIDType";
	private static final String PUBLICATION_CITATION= "publicationCitation";
	
	private static final String KEYWORD_VOCABULARY_URI = "keywordVocabularyURI";
	private static final String KEYWORD_VOCABULARY = "keywordVocabulary";
	private static final String KEYWORD_VALUE = "keywordValue";
	private static final String TOPIC_VOCABULARY_URI = "topicClassVocabURI";
	private static final String TOPIC_VOCABULARY = "topicClassVocab";
	private static final String TOPIC_VALUE = "topicClassValue";
	private static final String DATASET_DESC_VALUE = "dsDescriptionValue";
	private static final String DATASET_DESC_DATE = "dsDescriptionDate";
	private static final String DATASET_CONTACT_EMAIL = "datasetContactEmail";
	private static final String DATASET_CONTACT_NAME = "datasetContactName";
	private static final String DATASET_CONTACT_AFFILIATION = "datasetContactAffiliation";
	private static final String AUTHOR_IDENTIFIER_SCHEME = "authorIdentifierScheme";
	private static final String AUTHOR_IDENTIFIER = "authorIdentifier";
	private static final String AUTHOR_AFFILIATION = "authorAffiliation";
	private static final String AUTHOR_NAME = "authorName";

	public Dataset build(DatasetFacade facade) {
		DatasetVersion dv = new DatasetVersion();
		DataSetMetadataBlock blocks = new DataSetMetadataBlock();
		Citation citation = new Citation();

		List<CitationField> fields = createFields(facade, citation);

		dv.setMetadataBlocks(blocks);
		blocks.setCitation(citation);
		citation.setFields(fields);
		Dataset toSubmit = new Dataset();
		toSubmit.setDatasetVersion(dv);
		return toSubmit;
	}

	private List<CitationField> createFields(DatasetFacade facade, Citation citation) {
		List<CitationField> fields = new ArrayList<>();
		//mandatory fields
		addTitle(facade, fields);	
		addAuthors(facade, fields);
		addDescription(facade, fields);
		addKeywords(facade, fields);
		addTopicClassifications(facade, fields);
		addPublications(facade, fields);
		addLanguages(facade, fields);
		addNotes(facade, fields);
		addProducers(facade, fields);
		addProductionDate(facade, fields);
		addSubject(facade, fields);
		addContacts(facade, fields);
		
		//optional fields
		addDepositor(facade, fields);
		addSubTitle(facade, fields);
		addAlternativeTitle(facade, fields);
		addAlternativeURL(facade, fields);
		return fields;
	}

	

	private void addProductionDate(DatasetFacade facade, List<CitationField> fields) {
		if( facade.getProductionDate() != null) {
			CitationField prodDate = createPrimitiveSingleField("productionDate", isoDate(facade.getProductionDate()));
			fields.add(prodDate);
		}
		
	}

	private void addLanguages(DatasetFacade facade, List<CitationField> fields) {
		if (!facade.getLanguages().isEmpty()) {
			CitationField field = createControlledVocabField("language", true, facade.getLanguages());
			fields.add(field);
		}
	}

	private void addNotes(DatasetFacade facade, List<CitationField> fields) {
		if(!isEmpty(facade.getNote())){
			CitationField altUrl = createPrimitiveSingleField("notesText", facade.getNote());
			fields.add(altUrl);
		}		
	}

	private void addAlternativeURL(DatasetFacade facade, List<CitationField> fields) {
		if (facade.getAlternativeURL() != null) {
			CitationField altUrl = createPrimitiveSingleField("alternativeURL", facade.getAlternativeURL().toString());
			fields.add(altUrl);
		}
	}

	private void addAlternativeTitle(DatasetFacade facade, List<CitationField> fields) {
		if (!isEmpty(facade.getAlternativeTitle())) {
			CitationField title = createPrimitiveSingleField("alternativeTitle", facade.getAlternativeTitle());
			fields.add(title);
		}
	}

	private void addSubTitle(DatasetFacade facade, List<CitationField> fields) {
		if (!isEmpty(facade.getSubtitle())) {
			CitationField subtitle = createPrimitiveSingleField("subtitle", facade.getSubtitle());
			fields.add(subtitle);
		}
	}

	private void addSubject(DatasetFacade facade, List<CitationField> fields) {
		CitationField subject = createControlledVocabField("subject", true, Arrays.asList(facade.getSubject()));
		fields.add(subject);
	}

	private void addDescription(DatasetFacade facade, List<CitationField> fields) {
		List<DatasetDescription> descs = facade.getDescriptions();
		List<Map<String, Object>> descList = new ArrayList<>();
		for (DatasetDescription desc: descs) {
			Map<String, Object> map2 = new HashMap<>();
			CitationField descF = createPrimitiveSingleField(DATASET_DESC_VALUE, desc.getDescription());
			map2.put(DATASET_DESC_VALUE, descF);
			if(desc.getDate() != null) {
				CitationField dateF = createPrimitiveSingleField(DATASET_DESC_DATE, isoDate(desc.getDate()));
				map2.put(DATASET_DESC_DATE, dateF);
			}
			descList.add(map2);
		}
		
		CitationField desc = createCompoundField("dsDescription", true, descList);
		fields.add(desc);
	}
	
	private void addTopicClassifications(DatasetFacade facade, List<CitationField> fields) {
		List<DatasetTopicClassification> topics = facade.getTopicClassifications();
		List<Map<String, Object>> topicsList = new ArrayList<>();
		for (DatasetTopicClassification topic: topics) {
			Map<String, Object> map = new HashMap<>();
			addOptionalPrimitiveField(topic.getTopicClassValue(), map, TOPIC_VALUE);
			addOptionalPrimitiveField(topic.getTopicClassVocab(), map, TOPIC_VOCABULARY);
			addOptionalPrimitiveField(topic.getTopicClassVocabURI().toString(), map, TOPIC_VOCABULARY_URI);
			topicsList.add(map);
		}	
		CitationField topicClassifn = createCompoundField("topicClassification", true, topicsList);
		fields.add(topicClassifn);	
	}
	
	private void addProducers(DatasetFacade facade, List<CitationField> fields) {
		List<DatasetProducer> topics = facade.getProducers();
		List<Map<String, Object>> topicsList = new ArrayList<>();
		for (DatasetProducer topic: topics) {
			Map<String, Object> map = new HashMap<>();
			addOptionalPrimitiveField(topic.getName(), map, PRODUCER_NAME);
			addOptionalPrimitiveField(topic.getAbbreviation(), map, PRODUCER_ABBREVIATION);
			addOptionalPrimitiveField(topic.getAffiliation(), map, PRODUCER_AFFILIATION);
			addOptionalPrimitiveField(topic.getUrl().toString(), map, PRODUCER_URL);
			addOptionalPrimitiveField(topic.getLogoURL().toString(), map, PRODUCER_LOGO_URL);
			topicsList.add(map);
		}	
		CitationField topicClassifn = createCompoundField("producer", true, topicsList);
		fields.add(topicClassifn);	
		
	}
	
	private void addPublications(DatasetFacade facade, List<CitationField> fields) {
		List<DatasetPublication> publications = facade.getPublications();
		List<Map<String, Object>> list = new ArrayList<>();
		for (DatasetPublication publication: publications) {
			Map<String, Object> map = new HashMap<>();
			addOptionalPrimitiveField(publication.getPublicationCitation(), map, PUBLICATION_CITATION);
			addOptionalPrimitiveField(publication.getPublicationIdNumber(), map, PUBLICATION_ID);
			addOptionalPrimitiveField(publication.getPublicationURL().toString(), map, PUBLICATION_URL);
			if (publication.getPublicationIDType()!= null) {
				CitationField scheme = createControlledVocabField(PUBLICATION_ID_TYPE, false,
						asList(new String[] { publication.getPublicationIDType().name() }));
				map.put(PUBLICATION_ID_TYPE, scheme);
			}
			list.add(map);
		}	
		CitationField publication = createCompoundField("publication", true, list);
		fields.add(publication);	
	}
	
	private void addKeywords(DatasetFacade facade, List<CitationField> fields) {
		List<DatasetKeyword> keywords = facade.getKeywords();
		List<Map<String, Object>> keysList = new ArrayList<>();
		for (DatasetKeyword keyword: keywords) {
			Map<String, Object> map2 = new HashMap<>();
			CitationField descF = createPrimitiveSingleField(KEYWORD_VALUE, keyword.getValue());
			map2.put(KEYWORD_VALUE, descF);
			addOptionalPrimitiveField(keyword.getVocabulary(), map2, KEYWORD_VOCABULARY);
			addOptionalPrimitiveField(keyword.getVocabularyURI().toString(), map2, KEYWORD_VOCABULARY_URI);
			keysList.add(map2);
		}
		
		CitationField desc = createCompoundField("keyword", true, keysList);
		fields.add(desc);
		
	}

	private String isoDate(Date date) {
		return String.format("%tF", date);
	}

	private void addContacts (DatasetFacade facade, List<CitationField> fields) {
		List<DatasetContact> contacts = facade.getContacts();
		List<Map<String, Object>> contactsList = new ArrayList<>();

		for (DatasetContact contact : contacts) {
			Map<String, Object> map2 = new HashMap<>();
			CitationField email = createPrimitiveSingleField(DATASET_CONTACT_EMAIL, contact.getDatasetContactEmail());
			map2.put(DATASET_CONTACT_EMAIL, email);
			addOptionalPrimitiveField(contact.getDatasetContactName(), map2, DATASET_CONTACT_NAME);
			addOptionalPrimitiveField(contact.getDatasetContactAffiliation(), map2, DATASET_CONTACT_AFFILIATION);
			contactsList.add(map2);
		}
		CitationField contact = createCompoundField("datasetContact", true, contactsList);
		fields.add(contact);
	}

	private void addAuthors (DatasetFacade facade, List<CitationField> fields) {
		List<DatasetAuthor> authors = facade.getAuthors();
		List<Map<String, Object>> authorsMap = new ArrayList<>();
		for (DatasetAuthor author : authors) {
			Map<String, Object> map = new HashMap<>();
			CitationField authorName = createPrimitiveSingleField(AUTHOR_NAME, author.getAuthorName());
			map.put(AUTHOR_NAME, authorName);
			addOptionalPrimitiveField(author.getAuthorAffiliation(), map, AUTHOR_AFFILIATION);
			addOptionalPrimitiveField(author.getAuthorIdentifier(), map, AUTHOR_IDENTIFIER);
		
			if (!isEmpty(author.getAuthorIdentifierScheme())) {
				CitationField scheme = createControlledVocabField(AUTHOR_IDENTIFIER_SCHEME, false,
						asList(new String[] { author.getAuthorIdentifierScheme() }));
				map.put(AUTHOR_IDENTIFIER_SCHEME, scheme);
			}
			authorsMap.add(map);
		}
		CitationField toAdd = createCompoundField("author", true, authorsMap);
		fields.add(toAdd);
	}

	private void addOptionalPrimitiveField(String value,  Map<String, Object> map, String field) {
		if (!isEmpty(value)) {
			CitationField affil = createPrimitiveSingleField(field, value);
			map.put(field, affil);
		}
	}

	private void addTitle(DatasetFacade facade, List<CitationField> fields) {
		CitationField title = createPrimitiveSingleField("title", facade.getTitle());
		fields.add(title);
	}

	private void addDepositor(DatasetFacade facade, List<CitationField> fields) {
		CitationField deposit = createPrimitiveSingleField("depositor", facade.getDepositor());
		fields.add(deposit);
	}

	public CitationField createPrimitiveSingleField(String name, String value) {
		CitationField cf = new CitationField(name, CitationType.PRIMITIVE.toString(), false, value);
		return cf;
	}

	public CitationField createPrimitiveMultipleField(String name, String... value) {
		CitationField cf = new CitationField(name, CitationType.PRIMITIVE.toString(), true, value);
		return cf;
	}

	public CitationField createCompoundField(String name, boolean isMultiple, List<Map<String, Object>> values) {
		CitationField cf = null;
		checkArgs(isMultiple, values);
		if (isMultiple) {
			cf = new CitationField(name, CitationType.COMPOUND.toString(), isMultiple, values);
		} else {
			cf = new CitationField(name, CitationType.COMPOUND.toString(), isMultiple, values.get(0));
		}
		return cf;
	}

	private void checkArgs(boolean isMultiple, List<?> values) {
		if (!isMultiple && values.size() > 1) {
			throw new IllegalArgumentException(
					String.format("Field is not multiple but %d arguments were supplied", values.size()));
		}
	}

	public CitationField createControlledVocabField(String name, boolean isMultiple, List<String> values) {
		CitationField cf = null;
		checkArgs(isMultiple, values);
		if (isMultiple) {
			cf = new CitationField(name, CitationType.CONTROLLEDVOCABULARY.toString(), isMultiple, values);
		} else {
			cf = new CitationField(name, CitationType.CONTROLLEDVOCABULARY.toString(), isMultiple, values.get(0));
		}
		return cf;
	}

}
