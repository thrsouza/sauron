package com.github.thrsouza.sauron.application;

public interface UseCase<InputType, OutputType> {
    OutputType handle(InputType input);
}
