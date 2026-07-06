package com.svms.app.presentation.history;

import com.svms.app.data.repository.ViolationRepository;
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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<ViolationRepository> violationRepositoryProvider;

  public HistoryViewModel_Factory(Provider<ViolationRepository> violationRepositoryProvider) {
    this.violationRepositoryProvider = violationRepositoryProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(violationRepositoryProvider.get());
  }

  public static HistoryViewModel_Factory create(
      Provider<ViolationRepository> violationRepositoryProvider) {
    return new HistoryViewModel_Factory(violationRepositoryProvider);
  }

  public static HistoryViewModel newInstance(ViolationRepository violationRepository) {
    return new HistoryViewModel(violationRepository);
  }
}
