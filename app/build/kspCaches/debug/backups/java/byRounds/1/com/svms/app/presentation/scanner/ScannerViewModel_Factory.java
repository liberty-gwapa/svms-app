package com.svms.app.presentation.scanner;

import com.svms.app.data.repository.StudentRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class ScannerViewModel_Factory implements Factory<ScannerViewModel> {
  private final Provider<StudentRepository> studentRepositoryProvider;

  public ScannerViewModel_Factory(Provider<StudentRepository> studentRepositoryProvider) {
    this.studentRepositoryProvider = studentRepositoryProvider;
  }

  @Override
  public ScannerViewModel get() {
    return newInstance(studentRepositoryProvider.get());
  }

  public static ScannerViewModel_Factory create(
      Provider<StudentRepository> studentRepositoryProvider) {
    return new ScannerViewModel_Factory(studentRepositoryProvider);
  }

  public static ScannerViewModel newInstance(StudentRepository studentRepository) {
    return new ScannerViewModel(studentRepository);
  }
}
