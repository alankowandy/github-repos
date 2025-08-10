package com.example.github_repos.client;

import com.example.github_repos.client.model.GithubBranch;
import com.example.github_repos.client.model.GithubRepo;
import com.example.github_repos.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GithubClient {
    private final RestTemplate restTemplate;
    private final String apiBase;

    public GithubClient(RestTemplate restTemplate,
                        @Value("${github.api.url:https://api.github.com}") String apiBase) {
        this.restTemplate = restTemplate;
        this.apiBase = apiBase;
    }

    public List<GithubRepo> getGithubRepos(String username) {
        String url = apiBase + "/users/{username}/repos";
        try {
            ResponseEntity<List<GithubRepo>> resp = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<GithubRepo>>() {},
                    username
            );
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("Github user not found: " + username);
        }
    }

    public List<GithubBranch> listGithubBranches(String owner, String repo) {
        String url = apiBase + "/repos/{owner}/{repo}/branches";
        try {
            ResponseEntity<List<GithubBranch>> resp = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<GithubBranch>>() {},
                    owner, repo
            );
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return List.of();
        }
    }
}
