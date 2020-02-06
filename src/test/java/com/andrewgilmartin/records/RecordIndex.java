package com.andrewgilmartin.records;

import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.visitor.LuceneQueryVisitor;
import com.andrewgilmartin.common.query.visitor.ReduceQueryVisitor;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.MMapDirectory;

/**
 * Lucene index suitable for use in testing. The index is in memory.
 */
public class RecordIndex {

    public static final String ID_FIELD = "id";
    public static final String TITLE_FIELD = "title";
    public static final String CONTENT_FIELD = "content";

    private final IndexWriterConfig config;
    private final Directory index;
    private final IndexWriter writer;
    private final DirectoryReader reader;

    public RecordIndex(Path directory) throws IOException {
        this.config = new IndexWriterConfig(new StandardAnalyzer());
        this.index = new MMapDirectory(directory);
        this.writer = new IndexWriter(index, config);
        this.reader = DirectoryReader.open(writer);
    }

    public Adder getAdder() throws IOException {
        return new Adder();
    }

    public Searcher getSeacher() throws IOException {
        return new Searcher();
    }

    public class Adder implements AutoCloseable {

        @Override
        public void close() throws IOException {
            writer.commit();
        }

        public void add(Record record) {
            try {
                writer.addDocument(recordToDocument(record));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        protected Document recordToDocument(Record record) {
            Document document = new Document();
            document.add(new StringField(ID_FIELD, record.getId(), Field.Store.YES));
            document.add(new TextField(TITLE_FIELD, record.getTitle(), Field.Store.YES));
            document.add(new TextField(CONTENT_FIELD, record.getContent(), Field.Store.YES));
            return document;
        }

    }

    public class Searcher implements AutoCloseable {

        private final IndexSearcher searcher;

        public Searcher() throws IOException {
            this.searcher = new IndexSearcher(DirectoryReader.openIfChanged(reader, writer));
        }

        @Override
        public void close() {
            // empty
        }

        /**
         * Returns the record with the given id or null if none found.
         */
        public Record get(String id) {
            try {
                TopDocs hits = searcher.search(new TermQuery(new Term("id", id)), 1);
                if (hits.scoreDocs.length == 1) {
                    Record record = documentToRecord(searcher.getIndexReader().document(hits.scoreDocs[0].doc));
                    return record;
                }
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Returns the record hits matching the given query.
         */
        protected List<RecordHit> search(Query query, int limit) {
            return search(
                    new LuceneQueryVisitor().visitQuery(new ReduceQueryVisitor().visitQuery(query)),
                    limit
            );
        }

        /**
         * Returns the record hits matching the given query.
         */
        protected List<RecordHit> search(org.apache.lucene.search.Query query, int limit) {
            try {
                List<RecordHit> records = new LinkedList<>();
                TopDocs hits = searcher.search(query, limit);
                for (int i = 0; i < hits.scoreDocs.length; i++) {
                    Record record = documentToRecord(searcher.getIndexReader().document(hits.scoreDocs[i].doc));
                    RecordHit recordHit = new RecordHit(record, hits.scoreDocs[i].score);
                    records.add(recordHit);
                }
                return records;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        protected Record documentToRecord(Document document) {
            Record record = new Record(
                    document.getField(ID_FIELD).stringValue(),
                    document.getField(TITLE_FIELD).stringValue(),
                    document.getField(CONTENT_FIELD).stringValue()
            );
            return record;
        }

    }
}
