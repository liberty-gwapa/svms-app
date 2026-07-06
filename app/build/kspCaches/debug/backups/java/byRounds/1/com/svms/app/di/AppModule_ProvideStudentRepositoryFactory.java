package com.svms.app.di;

import com.svms.app.data.repository.StudentRepository;
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
public final class AppModule_ProvideStudentRepositoryFactory implements Factory<StudentRepository> {
  private final Provider<SupabaseClient> supabaseClientProvider;

  public AppModule_ProvideStudentRepositoryFactory(
      Provider<SupabaseClient> supabaseClientProvider) {
    this.supabaseClientProvider = supabaseClientProvider;
  }

  @Override
  public StudentRepository get() {
    return provideStudentRepository(supabaseClientProvider.get());
  }

  public static AppModule_ProvideStudentRepositoryFactory create(
      Provider<SupabaseClient> supabaseClientProvider) {
    return new AppModule_ProvideStudentRepositoryFactory(supabaseClientProvider);
  }

  public static StudentRepository provideStudentRepository(SupabaseClient supabaseClient) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideStudentRepository(supabaseClient));
  }
}
