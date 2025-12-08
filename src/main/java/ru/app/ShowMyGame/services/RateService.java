package ru.app.ShowMyGame.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.app.ShowMyGame.entities.Rate;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.repositories.RateRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RateService
{

    @Autowired
    private RateRepository rateRepository;

    public Rate rateProject(Project project, User user, Integer rateValue)
    {
        if (rateValue < 0 || rateValue > 5)
        {
            throw new IllegalArgumentException("Оценка должна быть от 0 до 5");
        }

        Optional<Rate> existingRate = rateRepository.getRateByProjectAndUser(project, user);
        if (existingRate.isPresent())
        {
            throw new RuntimeException("Вы уже оценили этот проект");
        }

        Rate rate = new Rate(project, user, rateValue);
        return rateRepository.addRate(rate);
    }

    public Rate updateRate(Integer rateId, Integer newRateValue)
    {
        if (newRateValue < 0 || newRateValue > 5)
        {
            throw new IllegalArgumentException("Оценка должна быть от 0 до 5");
        }
        Rate rate = rateRepository.getRateById(rateId).orElseThrow(() -> new RuntimeException("Оценка не найдена"));
        rate.setRate(newRateValue);
        return rateRepository.updateRate(rate);
    }

    public void deleteRate(Integer rateId)
    {
        Rate rate = rateRepository.getRateById(rateId).orElseThrow(() -> new RuntimeException("Оценка не найдена"));
        rateRepository.deleteRate(rate);
    }

    public List<Rate> getRatesByProject(Project project)
    {
        return rateRepository.getRatesByProject(project);
    }

    public List<Rate> getRatesByUser(User user)
    {
        return rateRepository.getRatesByUser(user);
    }

    public Optional<Rate> getUserRateForProject(Project project, User user)
    {
        return rateRepository.getRateByProjectAndUser(project, user);
    }

    public Double getAverageRateForProject(Project project)
    {
        List<Rate> rates = rateRepository.getRatesByProject(project);
        Double sum = 0d;
        for (Rate rate : rates)
        {
            sum += rate.getRate();
        }
        Integer count = getRateCountForProject(project);

        return count != 0 ? sum / count : 0d;
    }

    public Integer getRateCountForProject(Project project)
    {
        return rateRepository.getRatesByProject(project).size();
    }

    public Rate getRateById(Integer rateId)
    {
        return rateRepository.getRateById(rateId).orElseThrow(() -> new RuntimeException("Оценка не найдена"));
    }
}