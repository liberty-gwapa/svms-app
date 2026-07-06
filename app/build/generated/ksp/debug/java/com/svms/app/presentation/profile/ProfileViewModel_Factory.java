package com.svms.app.presentation.profile;

import com.svms.app.data.repository.AuthRepository;
import com.svms.app.data.repository.CloudinaryRepository;
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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<CloudinaryRepository> cloudinaryRepositoryProvider;

  public ProfileViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<CloudinaryRepository> cloudinaryRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.cloudinaryRepositoryProvider = cloudinaryRepositoryProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(authRepositoryProvider.get(), cloudinaryRepositoryProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<CloudinaryRepository> cloudinaryRepositoryProvider) {
    return new ProfileViewModel_Factory(authRepositoryProvider, cloudinaryRepositoryProvider);
  }

  public static ProfileViewModel newInstance(AuthRepository authRepository,
      CloudinaryRepository cloudinaryRepository) {
    return new ProfileViewModel(authRepository, cloudinaryRepository);
  }
}
