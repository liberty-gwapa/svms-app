package com.svms.app.di;

import android.content.Context;
import com.svms.app.data.repository.AuthRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideAuthRepositoryFactory implements Factory<AuthRepository> {
  private final Provider<SupabaseClient> supabaseClientProvider;

  private final Provider<Context> contextProvider;

  public AppModule_ProvideAuthRepositoryFactory(Provider<SupabaseClient> supabaseClientProvider,
      Provider<Context> contextProvider) {
    this.supabaseClientProvider = supabaseClientProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public AuthRepository get() {
    return provideAuthRepository(supabaseClientProvider.get(), contextProvider.get());
  }

  public static AppModule_ProvideAuthRepositoryFactory create(
      Provider<SupabaseClient> supabaseClientProvider, Provider<Context> contextProvider) {
    return new AppModule_ProvideAuthRepositoryFactory(supabaseClientProvider, contextProvider);
  }

  public static AuthRepository provideAuthRepository(SupabaseClient supabaseClient,
      Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAuthRepository(supabaseClient, context));
  }
}
