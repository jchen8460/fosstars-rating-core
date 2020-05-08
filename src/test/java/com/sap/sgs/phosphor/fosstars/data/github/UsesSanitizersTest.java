package com.sap.sgs.phosphor.fosstars.data.github;

import static com.sap.sgs.phosphor.fosstars.model.feature.oss.OssFeatures.USES_ADDRESS_SANITIZER;
import static com.sap.sgs.phosphor.fosstars.model.feature.oss.OssFeatures.USES_MEMORY_SANITIZER;
import static com.sap.sgs.phosphor.fosstars.model.feature.oss.OssFeatures.USES_UNDEFINED_BEHAVIOR_SANITIZER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sap.sgs.phosphor.fosstars.model.Value;
import com.sap.sgs.phosphor.fosstars.model.ValueSet;
import com.sap.sgs.phosphor.fosstars.tool.github.GitHubProject;
import com.sap.sgs.phosphor.fosstars.tool.github.GitHubProjectValueCache;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class UsesSanitizersTest extends TestGitHubDataFetcherHolder {

  @Test
  public void testFetchValuesForWithAllSanitizers() throws IOException {
    String content = String.join("\n", new String[] {
        "first line",
        "--debug -fsanitize=memory --another-option",
        "--option -fsanitize=undefined,address --another-option --debug",
        "another line",
    });

    testProvider(content,
        USES_ADDRESS_SANITIZER.value(true),
        USES_MEMORY_SANITIZER.value(true),
        USES_UNDEFINED_BEHAVIOR_SANITIZER.value(true));
  }

  @Test
  public void testFetchValuesForWithSomeSanitizers() throws IOException {
    testProvider("-fsanitize= address", USES_ADDRESS_SANITIZER.value(true));
    testProvider("-fsanitize=address ", USES_ADDRESS_SANITIZER.value(true));
    testProvider("--test  -fsanitize=address,    memory  --other=a,b --test",
        USES_ADDRESS_SANITIZER.value(true), USES_MEMORY_SANITIZER.value(true));

    String content = String.join("\n", new String[] {
        "first line",
        "-fsanitize=undefined,address",
        "another line",
    });

    testProvider(content,
        USES_ADDRESS_SANITIZER.value(true),
        USES_MEMORY_SANITIZER.value(false),
        USES_UNDEFINED_BEHAVIOR_SANITIZER.value(true));
  }

  @Test
  public void testFetchValuesForWithoutSanitizers() throws IOException {
    String content = String.join("\n", new String[] {
        "first line",
        "another line",
    });

    testProvider(content,
        USES_ADDRESS_SANITIZER.value(false),
        USES_MEMORY_SANITIZER.value(false),
        USES_UNDEFINED_BEHAVIOR_SANITIZER.value(false));
  }

  private void testProvider(String content, Value... expectedValues) throws IOException {
    UsesSanitizers provider = new UsesSanitizers(fetcher);
    provider.set(new GitHubProjectValueCache());

    final LocalRepository repository = mock(LocalRepository.class);
    when(repository.files(any()))
        .thenReturn(Collections.singletonList(Paths.get("CMakeLists.txt")));

    when(repository.file(any(Path.class))).thenReturn(Optional.of(content));

    GitHubProject project = new GitHubProject("org", "test");
    fetcher.addForTesting(project, repository);

    ValueSet values = provider.fetchValuesFor(project);
    assertEquals(3, values.size());

    for (Value expectedValue : expectedValues) {
      Optional<Value> something = values.of(expectedValue.feature());
      assertTrue(something.isPresent());
      Value actualValue = something.get();
      assertEquals(expectedValue, actualValue);
    }
  }

  @Test
  public void testMaybeBuildConfig() {
    assertTrue(UsesSanitizers.maybeBuildConfig(Paths.get("configure.ac")));
    assertTrue(UsesSanitizers.maybeBuildConfig(Paths.get("Configure")));
    assertFalse(UsesSanitizers.maybeBuildConfig(Paths.get("README.md")));
  }

  @Test
  public void testLookForSanitizers() throws IOException {
    String content = String.join("\n", new String[] {
        "first line",
        "-fsanitize=address",
        "another line",
        "--debug -fsanitize=memory --another-option",
        "--option -fsanitize=undefined,address --another-option --debug"
    });
    List<String> options = UsesSanitizers.lookForSanitizers(content);
    assertEquals(Arrays.asList("address", "memory", "undefined", "address"), options);
  }

  @Test
  public void testParse() {
    assertTrue(UsesSanitizers.parse("something else").isEmpty());
    assertEquals(
        Arrays.asList("address"),
        UsesSanitizers.parse("something else -fsanitize=address"));
    assertEquals(
        Arrays.asList("address"),
        UsesSanitizers.parse("-fsanitize=address"));
    assertEquals(
        Arrays.asList("address", "memory", "test"),
        UsesSanitizers.parse("-fsanitize=address,memory,test"));
    assertEquals(
        Arrays.asList("address", "memory", "test"),
        UsesSanitizers.parse("-fsanitize=address,memory --opt -fsanitize=test --another"));
    assertEquals(
        Arrays.asList("address", "memory", "test"),
        UsesSanitizers.parse("-fsanitize=  address, memory --opt -fsanitize=test --another"));
  }
}