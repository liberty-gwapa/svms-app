package com.svms.app.presentation.notification;

import com.svms.app.data.repository.AuthRepository;
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
public final class NotificationViewModel_Factory implements Factory<NotificationViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  public NotificationViewModel_Factory(Provider<AuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public NotificationViewModel get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static NotificationViewModel_Factory create(
      Provider<AuthRepository> authRepositoryProvider) {
    return new NotificationViewModel_Factory(authRepositoryProvider);
  }

  public static NotificationViewModel newInstance(AuthRepository authRepository) {
    return new NotificationViewModel(authRepository);
  }
}
