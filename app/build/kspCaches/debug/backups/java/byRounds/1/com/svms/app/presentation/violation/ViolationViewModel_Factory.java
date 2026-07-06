package com.svms.app.presentation.violation;

import com.svms.app.data.repository.AuthRepository;
import com.svms.app.data.repository.CloudinaryRepository;
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
public final class ViolationViewModel_Factory implements Factory<ViolationViewModel> {
  private final Provider<ViolationRepository> violationRepositoryProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<CloudinaryRepository> cloudinaryRepositoryProvider;

  public ViolationViewModel_Factory(Provider<ViolationRepository> violationRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<CloudinaryRepository> cloudinaryRepositoryProvider) {
    this.violationRepositoryProvider = violationRepositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.cloudinaryRepositoryProvider = cloudinaryRepositoryProvider;
  }

  @Override
  public ViolationViewModel get() {
    return newInstance(violationRepositoryProvider.get(), authRepositoryProvider.get(), cloudinaryRepositoryProvider.get());
  }

  public static ViolationViewModel_Factory create(
      Provider<ViolationRepository> violationRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<CloudinaryRepository> cloudinaryRepositoryProvider) {
    return new ViolationViewModel_Factory(violationRepositoryProvider, authRepositoryProvider, cloudinaryRepositoryProvider);
  }

  public static ViolationViewModel newInstance(ViolationRepository violationRepository,
      AuthRepository authRepository, CloudinaryRepository cloudinaryRepository) {
    return new ViolationViewModel(violationRepository, authRepository, cloudinaryRepository);
  }
}
