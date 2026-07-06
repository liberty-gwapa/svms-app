package com.svms.app.presentation.violation.details;

import androidx.lifecycle.SavedStateHandle;
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
public final class ViolationDetailsViewModel_Factory implements Factory<ViolationDetailsViewModel> {
  private final Provider<ViolationRepository> violationRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ViolationDetailsViewModel_Factory(
      Provider<ViolationRepository> violationRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.violationRepositoryProvider = violationRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ViolationDetailsViewModel get() {
    return newInstance(violationRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static ViolationDetailsViewModel_Factory create(
      Provider<ViolationRepository> violationRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ViolationDetailsViewModel_Factory(violationRepositoryProvider, savedStateHandleProvider);
  }

  public static ViolationDetailsViewModel newInstance(ViolationRepository violationRepository,
      SavedStateHandle savedStateHandle) {
    return new ViolationDetailsViewModel(violationRepository, savedStateHandle);
  }
}
