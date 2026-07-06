package com.svms.app.presentation.history;

import com.svms.app.data.repository.AuthRepository;
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

  private final Provider<AuthRepository> authRepositoryProvider;

  public HistoryViewModel_Factory(Provider<ViolationRepository> violationRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    this.violationRepositoryProvider = violationRepositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(violationRepositoryProvider.get(), authRepositoryProvider.get());
  }

  public static HistoryViewModel_Factory create(
      Provider<ViolationRepository> violationRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    return new HistoryViewModel_Factory(violationRepositoryProvider, authRepositoryProvider);
  }

  public static HistoryViewModel newInstance(ViolationRepository violationRepository,
      AuthRepository authRepository) {
    return new HistoryViewModel(violationRepository, authRepository);
  }
}
