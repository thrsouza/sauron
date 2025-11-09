package com.github.thrsouza.sauron.application.usecases;

public interface UseCase<InputType, OutputType> {
    OutputType execute(InputType input);
}
