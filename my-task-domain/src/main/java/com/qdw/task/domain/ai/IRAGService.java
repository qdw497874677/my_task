package com.qdw.task.domain.ai;

import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRAGService {

    List<String> queryRagTagList();

    List<Document> vectorStore();

    String uploadFile(String ragTag, List<MultipartFile> files);

    String uploadDocument(String ragTag, String text);

    String uploadDocument(String ragTag, List<Document> documentList);

    String analyzeGitRepository(String repoUrl, String userName, String token) throws Exception;

}
