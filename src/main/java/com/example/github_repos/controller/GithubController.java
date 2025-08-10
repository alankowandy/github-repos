package com.example.github_repos.controller;

import com.example.github_repos.dto.RepoDto;
import com.example.github_repos.service.GithubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GithubController {
    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/{username}/repos")
    public List<RepoDto> getRepos(@PathVariable String username) {
        return githubService.getUserReposWithBranches(username);
    }
}
