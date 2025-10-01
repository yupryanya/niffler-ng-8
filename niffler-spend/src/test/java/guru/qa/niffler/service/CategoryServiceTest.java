package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Test
  void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
    final String username = "not_found";
    final UUID id = UUID.randomUUID();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.empty());

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "",
        username,
        true
    );

    CategoryNotFoundException ex = Assertions.assertThrows(
        CategoryNotFoundException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t find category by id: '" + id + "'",
        ex.getMessage()
    );
  }

  @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
  @ParameterizedTest
  void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        catName,
        username,
        true
    );

    InvalidCategoryNameException ex = Assertions.assertThrows(
        InvalidCategoryNameException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t add category with name: '" + catName + "'",
        ex.getMessage()
    );
  }

  @Test
  void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();
    cat.setId(id);
    cat.setUsername(username);
    cat.setName("Магазины");
    cat.setArchived(false);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "Бары",
        username,
        true
    );

    categoryService.update(categoryJson);
    ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(argumentCaptor.capture());
    assertEquals("Бары", argumentCaptor.getValue().getName());
    assertEquals("duck", argumentCaptor.getValue().getUsername());
    assertTrue(argumentCaptor.getValue().isArchived());
    assertEquals(id, argumentCaptor.getValue().getId());
  }

  @Test
  void getAllCategoriesShouldFilterArchived(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final CategoryEntity active = new CategoryEntity();
    active.setId(UUID.randomUUID());
    active.setName("Active");
    active.setUsername(username);
    active.setArchived(false);

    final CategoryEntity archived = new CategoryEntity();
    archived.setId(UUID.randomUUID());
    archived.setName("Old");
    archived.setUsername(username);
    archived.setArchived(true);

    Mockito.when(categoryRepository.findAllByUsernameOrderByName(username))
        .thenReturn(List.of(active, archived));

    CategoryService categoryService = new CategoryService(categoryRepository);

    List<CategoryJson> result = categoryService.getAllCategories(username, true);
    assertEquals(1, result.size());
    assertEquals("Active", result.get(0).name());

    List<CategoryJson> all = categoryService.getAllCategories(username, false);
    assertEquals(2, all.size());
  }

  @Test
  void updateShouldThrowWhenUnarchivingExceedsMaxCategories(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity archivedCat = new CategoryEntity();
    archivedCat.setId(id);
    archivedCat.setUsername(username);
    archivedCat.setName("Old cat");
    archivedCat.setArchived(true);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(archivedCat));
    Mockito.when(categoryRepository.countByUsernameAndArchived(username, false))
        .thenReturn(8L);

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "Old cat",
        username,
        false
    );

    TooManyCategoriesException ex = Assertions.assertThrows(
        TooManyCategoriesException.class,
        () -> categoryService.update(categoryJson)
    );

    assertEquals("Can`t unarchive category for user: 'duck'", ex.getMessage());
  }

  @Test
  void saveShouldThrowWhenExceedsMaxCategories(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final String name = "New Category";

    Mockito.when(categoryRepository.countByUsernameAndArchived(username, false))
        .thenReturn(8L);

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        UUID.randomUUID(),
        name,
        username,
        false
    );

    TooManyCategoriesException ex = Assertions.assertThrows(
        TooManyCategoriesException.class,
        () -> categoryService.save(categoryJson)
    );

    assertEquals("Can`t add over than 8 categories for user: 'duck'", ex.getMessage());
  }

  @Test
  void saveShouldPersistNewCategory(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final String name = "Sport";

    Mockito.when(categoryRepository.countByUsernameAndArchived(username, false))
        .thenReturn(3L);
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(invocation -> {
          CategoryEntity ce = invocation.getArgument(0);
          ce.setId(UUID.randomUUID());
          return ce;
        });

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        UUID.randomUUID(),
        name,
        username,
        false
    );

    CategoryEntity saved = categoryService.save(categoryJson);

    assertEquals(name, saved.getName());
    assertEquals(username, saved.getUsername());
    assertFalse(saved.isArchived());
    assertNotNull(saved.getId());
  }
}