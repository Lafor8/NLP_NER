package informationRetrieval.models;

import informationRetrieval.io.ArticleTxtFileReader;
import informationRetrieval.normalization.Normalizer;
import informationRetrieval.tokenization.Tokenizer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DocumentManager {

	private static DocumentManager instance;

	public static DocumentManager getInstance() {
		if (instance == null)
			instance = new DocumentManager();
		return instance;
	}

	private LinkedHashMap<Integer, Document> documents = new LinkedHashMap<Integer, Document>();
	private int documentCount = 0;

	public void populate(String folderPath) {
		// list all files
		// list all folders, recurse
		File folder = new File(folderPath);
		File[] list = folder.listFiles();

		for (File file : list) {
			if (file.isFile()) {
				if (file.getName().toLowerCase().contains(".txt")) {
					try {
						Document currDocument = ArticleTxtFileReader.parseToDocument(file);
						documentCount++;
						currDocument.documentNumber = documentCount;
						documents.put(currDocument.documentNumber, currDocument);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (file.isDirectory()) {
				populate(file.getAbsolutePath());
			}
		}

		// System.out.println("Done reading " + documents.size() +
		// " txt files.");
	}

	public void tokenize(Tokenizer tokenizer) {
		for (Map.Entry<Integer, Document> entry : documents.entrySet()) {
			Document document = entry.getValue();
			document.tokens = tokenizer.tokenize(document.text);
			// System.out.println("There are " + document.tokens.size() +
			// " tokens in " + document.filePath);
		}
	}

	public void normalize(Normalizer normalizer) {
		for (Map.Entry<Integer, Document> entry : documents.entrySet()) {

			Document document = entry.getValue();
			List<String> normalizedTokens = new ArrayList<String>();

			for (String token : document.tokens) {
				String normalizedToken = normalizer.normalize(token);
				if (normalizedToken != null)
					normalizedTokens.add(normalizedToken);
			}

			document.tokens = normalizedTokens;
		}

	}

	public List<Document> getDocumentsAsList() {
		return new ArrayList<Document>(documents.values());
	}

	public Document getDocumentByNumber(int number) {
		return documents.get(number);
	}
}
