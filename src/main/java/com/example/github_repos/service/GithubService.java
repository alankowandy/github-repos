package com.example.github_repos.service;

import com.example.github_repos.client.GithubClient;
import com.example.github_repos.client.model.GithubBranch;
import com.example.github_repos.client.model.GithubRepo;
import com.example.github_repos.dto.BranchDto;
import com.example.github_repos.dto.RepoDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubService {
    private final GithubClient client;

    public GithubService(GithubClient client) {
        this.client = client;
    }

    public List<RepoDto> getUserReposWithBranches(String username) {
        List<GithubRepo> repos = client.getGithubRepos(username);
        if (repos == null || repos.isEmpty()) {
            return List.of();
        }

        return repos.stream()
                .filter(r -> !r.fork())
                .map(r -> {
                    String owner = r.owner().login();
                    List<GithubBranch> branches = client.listGithubBranches(owner, r.name());
                    List<BranchDto> branchDtos = (branches == null ? List.<BranchDto>of() :
                            branches.stream()
                                    .map(b -> new BranchDto(b.name(), b.commit() != null ? b.commit().sha() : null))
                                    .collect(Collectors.toList()));
                    return new RepoDto(r.name(), owner, branchDtos);
                })
                .collect(Collectors.toList());
    }
}
