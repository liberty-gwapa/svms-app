package com.svms.app.di;

import com.svms.app.data.repository.ViolationRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideViolationRepositoryFactory implements Factory<ViolationRepository> {
  private final Provider<SupabaseClient> supabaseClientProvider;

  public AppModule_ProvideViolationRepositoryFactory(
      Provider<SupabaseClient> supabaseClientProvider) {
    this.supabaseClientProvider = supabaseClientProvider;
  }

  @Override
  public ViolationRepository get() {
    return provideViolationRepository(supabaseClientProvider.get());
  }

  public static AppModule_ProvideViolationRepositoryFactory create(
      Provider<SupabaseClient> supabaseClientProvider) {
    return new AppModule_ProvideViolationRepositoryFactory(supabaseClientProvider);
  }

  public static ViolationRepository provideViolationRepository(SupabaseClient supabaseClient) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideViolationRepository(supabaseClient));
  }
}
