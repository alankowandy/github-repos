package com.example.github_repos;

import com.example.github_repos.client.GithubClient;
import com.example.github_repos.client.model.GithubBranch;
import com.example.github_repos.client.model.GithubRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.GET;

@SpringBootTest
class GithubIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GithubClient githubClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void happyPathTest() {
        String reposJson = """
            [
              {"name": "my-repo", "owner": {"login": "octocat"}, "fork": false},
              {"name": "another-repo", "owner": {"login": "octocat"}, "fork": false}
            ]
        """;
        mockServer.expect(requestTo("https://api.github.com/users/octocat/repos"))
                .andExpect(method(GET))
                .andRespond(withSuccess(reposJson, MediaType.APPLICATION_JSON));

        String branchesMyRepo = """
            [
              {"name": "main", "commit": {"sha": "abc123"}},
              {"name": "dev", "commit": {"sha": "def456"}}
            ]
        """;
        mockServer.expect(requestTo("https://api.github.com/repos/octocat/my-repo/branches"))
                .andExpect(method(GET))
                .andRespond(withSuccess(branchesMyRepo, MediaType.APPLICATION_JSON));

        String branchesAnother = """
            [
              {"name": "main", "commit": {"sha": "789ghi"}}
            ]
        """;
        mockServer.expect(requestTo("https://api.github.com/repos/octocat/another-repo/branches"))
                .andExpect(method(GET))
                .andRespond(withSuccess(branchesAnother, MediaType.APPLICATION_JSON));

        List<GithubRepo> repos = githubClient.getGithubRepos("octocat");
        List<GithubBranch> repo1Branches = githubClient.listGithubBranches("octocat", "my-repo");
        List<GithubBranch> repo2Branches = githubClient.listGithubBranches("octocat", "another-repo");

        assertThat(repos).hasSize(2);
        assertThat(repo1Branches).extracting(GithubBranch::name)
                .containsExactly("main", "dev");
        assertThat(repo2Branches).extracting(GithubBranch::commit)
                .extracting(GithubBranch.Commit::sha)
                .containsExactly("789ghi");
    }
}