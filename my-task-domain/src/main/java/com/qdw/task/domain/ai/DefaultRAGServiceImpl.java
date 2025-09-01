package com.qdw.task.domain.ai;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class DefaultRAGServiceImpl implements IRAGService {


//    private VectorStore vectorStore;
    @Autowired
    private PgVectorStore pgVectorStore;

    @Autowired
    private TokenTextSplitter tokenTextSplitter;


    @Override
    public List<String> queryRagTagList() {
        return null;
    }

    @Override
    public List<Document> vectorStore() {
        List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));

        pgVectorStore.add(documents);
        List<Document> results = this.pgVectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
        return results;
    }

    @Override
    public String uploadFile(String ragTag, List<MultipartFile> files) {
//        TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());

        return null;
    }

    @Override
    public String uploadDocument(String ragTag, String text) {
        // 创建文档对象
        Document document = new Document(text);
        
        // 调用重载的 uploadDocument 方法处理文档列表
        return this.uploadDocument(ragTag, List.of(document));
    }

    @Override
    public String uploadDocument(String ragTag, List<Document> documentList) {
//        TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
//        List<Document> documents = documentReader.get();
        List<Document> documentSplitterList = tokenTextSplitter.apply(documentList);
        documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", ragTag));
        pgVectorStore.accept(documentSplitterList);
        return "成功上传 " + documentSplitterList.size() + " 个文档片段到标签 '" + ragTag + "'";
    }


    @Override
    public String analyzeGitRepository(String repoUrl, String userName, String token) throws Exception {
        return null;
    }
}
