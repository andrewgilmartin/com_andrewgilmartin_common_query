# com_andrewgilmartin_common_query

A set of common classes for full-text search queries mostly targeted at Lucene. 
I originally wrote these while working at Ingenta on their Bingo full text 
search service. Bingo pre-dated Solr. Using the Lucene search syntax was 
cumbersome and, if I remember correctly, not all Lucene query features could 
be used with it. In any case, it made more sense to construct a query as a Java 
data structure that could then be manipulated by data structure vistors. Using
the visitor pattern is powerful technique for manipulating queries.
