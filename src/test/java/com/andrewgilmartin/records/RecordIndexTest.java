package com.andrewgilmartin.records;

import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.OrQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.QueryUtils;
import com.andrewgilmartin.common.query.TermQuery;
import com.andrewgilmartin.common.query.visitor.LuceneQueryVisitor;
import com.andrewgilmartin.common.query.visitor.ReduceQueryVisitor;
import com.andrewgilmartin.records.RecordIndex.Adder;
import com.andrewgilmartin.records.RecordIndex.Searcher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;

public class RecordIndexTest {

    public static final Record[] records = new Record[]{
        new Record("1", "aaa bbb ccc ddd    ", "eee fff hhh iii        "),
        new Record("2", "aaa     ccc        ", "eee     hhh     jjj    "),
        new Record("3", "aaa bbb            ", "eee fff             kkk"),
        new Record("4", "aaa             xxx", "eee     hhh         kkk"),
        new Record("5", "    bbb         xxx", "eee fff             kkk")
    };

    @Test
    public void testGet() {
        buildAndTest((searcher) -> {
            for (Record record : records) {
                Record r = searcher.get(record.getId());
                assertNotNull(r);
                assertEquals(record.getId(), r.getId());
                assertEquals(record.getTitle(), r.getTitle());
                assertEquals(record.getContent(), r.getContent());
            }
        });
    }

    @Test
    public void testSearch1() {
        buildAndTest((searcher) -> {
            Query query = new TermQuery(RecordIndex.TITLE_FIELD, "aaa");
            List<RecordHit> hits = search(searcher, query);
            assertEquals(4, hits.size());
        });
    }

    @Test
    public void testSearch2() {
        buildAndTest((searcher) -> {
            Query query = new TermQuery(RecordIndex.TITLE_FIELD, "bbb");
            List<RecordHit> hits = search(searcher, query);
            assertEquals(3, hits.size());
        });
    }

    @Test
    public void testSearch3() {
        buildAndTest((searcher) -> {
            Query query = new TermQuery(RecordIndex.TITLE_FIELD, "ddd");
            List<RecordHit> hits = search(searcher, query);
            assertEquals(1, hits.size());
            assertEquals(records[0].getId(), hits.get(0).getRecord().getId());
        });
    }

    @Test
    public void testSearch4() {
        buildAndTest((searcher) -> {
            Query query = new AndQuery(
                    new TermQuery(RecordIndex.TITLE_FIELD, "bbb"),
                    new TermQuery(RecordIndex.CONTENT_FIELD, "hhh")
            );
            List<RecordHit> hits = search(searcher, query);
            assertEquals(1, hits.size());
            assertEquals(records[0].getId(), hits.get(0).getRecord().getId());
        });
    }

    @Test
    public void testSearch5() {
        buildAndTest((searcher) -> {
            Query query = new AndQuery(
                    new TermQuery(RecordIndex.TITLE_FIELD, "aaa"),
                    new TermQuery(RecordIndex.TITLE_FIELD, "ccc"),
                    new TermQuery(RecordIndex.CONTENT_FIELD, "hhh")
            );
            List<RecordHit> hits = search(searcher, query);
            assertEquals(2, hits.size());
            assertEquals(records[0].getId(), hits.get(0).getRecord().getId());
            assertEquals(records[1].getId(), hits.get(1).getRecord().getId());
        });
    }

    @Test
    public void testSearch6() {
        buildAndTest((searcher) -> {
            Query query = new AndQuery(
                    new TermQuery(RecordIndex.TITLE_FIELD, "bbb"),
                    new TermQuery(RecordIndex.CONTENT_FIELD, "lll")
            );
            List<RecordHit> hits = search(searcher, query);
            assertEquals(0, hits.size());
        });
    }

    @Test
    public void testSearch7() {
        buildAndTest((searcher) -> {
            Query query = new OrQuery(
                    new TermQuery(RecordIndex.TITLE_FIELD, "bbb"),
                    new TermQuery(RecordIndex.CONTENT_FIELD, "lll")
            );
            List<RecordHit> hits = search(searcher, query);
            assertEquals(3, hits.size());
            assertEquals(records[0].getId(), hits.get(0).getRecord().getId());
            assertEquals(records[2].getId(), hits.get(1).getRecord().getId());
            assertEquals(records[4].getId(), hits.get(2).getRecord().getId());
        });
    }

    @Test
    public void testSearch8() {
        buildAndTest((searcher) -> {
            Query query = new AndQuery(
                    new TermQuery(RecordIndex.TITLE_FIELD, "bbb"),
                    new NotQuery(new TermQuery(RecordIndex.CONTENT_FIELD, "kkk"))
            );
            List<RecordHit> hits = search(searcher, query);
            assertEquals(1, hits.size());
            assertEquals(records[0].getId(), hits.get(0).getRecord().getId());
        });
    }

    @Test
    public void testSearch9() {
        buildAndTest((searcher) -> {
            Query query = new AndQuery(
                    new OrQuery(
                            new TermQuery(RecordIndex.TITLE_FIELD, "aaa"),
                            new TermQuery(RecordIndex.TITLE_FIELD, "xxx")
                    ),
                    new NotQuery(
                            new TermQuery(RecordIndex.CONTENT_FIELD, "iii"),
                            new TermQuery(RecordIndex.CONTENT_FIELD, "kkk")
                    )
            );
            List<RecordHit> hits = search(searcher, query);
            assertEquals(1, hits.size());
            assertEquals(records[1].getId(), hits.get(0).getRecord().getId());
        });
    }

    @Test
    public void testSearch10() {
        buildAndTest((searcher) -> {
            Query query = new OrQuery(
                    new AndQuery(
                            new TermQuery(RecordIndex.TITLE_FIELD, "aaa"),
                            new NotQuery(
                                    new TermQuery(RecordIndex.CONTENT_FIELD, "kkk")
                            )
                    ),
                    new AndQuery(
                            new TermQuery(RecordIndex.TITLE_FIELD, "xxx"),
                            new NotQuery(
                                    new TermQuery(RecordIndex.CONTENT_FIELD, "hhh")
                            )
                    )
            );
            List<RecordHit> hits = search(searcher, query);
            assertEquals(3, hits.size());
            assertEquals(records[0].getId(), hits.get(0).getRecord().getId());
            assertEquals(records[1].getId(), hits.get(1).getRecord().getId());
            assertEquals(records[4].getId(), hits.get(2).getRecord().getId());
        });
    }

    private List<RecordHit> search(Searcher searcher, Query query) {
        // dumpLucene(query);
        List<RecordHit> hits = searcher.search(query, 10);
        Collections.sort(hits, (a, b) -> a.getRecord().getId().compareTo(b.getRecord().getId()));
        return hits;
    }

    private void buildAndTest(Consumer<Searcher> f) {
        try {
            Path directory = Files.createTempDirectory("test");
            try {
                RecordIndex index = new RecordIndex(directory);
                try (Adder adder = index.getAdder()) {
                    for (Record record : records) {
                        adder.add(record);
                    }
                }
                try (Searcher searcher = index.getSeacher()) {
                    f.accept(searcher);
                }
            } finally {
                deletePath(directory);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deletePath(Path path) {
        try {
            if (Files.isDirectory(path)) {
                Files.list(path).forEach(p -> deletePath(p));
            }
            Files.delete(path);
        } catch (IOException e) {
            fail("unable to remove test index " + path);
        }
    }

    private void dumpLucene(Query query) {
        System.out.println("=====");
        System.out.println(
                QueryUtils.dump(
                        new LuceneQueryVisitor().visitQuery(new ReduceQueryVisitor().visitQuery(query))
                )
        );
        System.out.println("=====");
    }
}
