package kl.socialnetwork.servicesImpl;

import kl.socialnetwork.domain.entities.Comment;
import kl.socialnetwork.domain.entities.Post;
import kl.socialnetwork.domain.entities.User;
import kl.socialnetwork.domain.modles.bindingModels.comment.CommentCreateBindingModel;
import kl.socialnetwork.domain.modles.bindingModels.post.PostCreateBindingModel;
import kl.socialnetwork.domain.modles.serviceModels.PostServiceModel;
import kl.socialnetwork.repositories.CommentRepository;
import kl.socialnetwork.repositories.PostRepository;
import kl.socialnetwork.repositories.UserRepository;
import kl.socialnetwork.services.CommentService;
import kl.socialnetwork.services.PostService;
import kl.socialnetwork.services.UserService;
import kl.socialnetwork.testUtils.CommentsUtils;
import kl.socialnetwork.testUtils.PostsUtils;
import kl.socialnetwork.testUtils.UsersUtils;
import kl.socialnetwork.utils.responseHandler.exceptions.CustomException;
import kl.socialnetwork.validations.serviceValidation.services.CommentValidationService;
import kl.socialnetwork.validations.serviceValidation.services.PostValidationService;
import kl.socialnetwork.validations.serviceValidation.services.UserValidationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommentServiceTests {

    @Autowired
    private CommentService commentService;

    @MockBean
    private PostRepository mockPostRepository;

    @MockBean
    private UserRepository mockUserRepository;

    @MockBean
    private CommentRepository mockCommentRepository;

    @MockBean
    private PostValidationService mockPostValidationService;

    @MockBean
    private UserValidationService mockUserValidationService;

    @MockBean
    private CommentValidationService mockCommentValidationService;

    @Before
    public void setUpTest() {

    }

    @Test
    public void createComment_whenUsersAndPostAndCommentCreateBindingModelAreValid_createComment() throws Exception {
        // Arrange
        CommentCreateBindingModel commentCreateBindingModel = CommentsUtils.getCommentCreateBindingModel(1).get(0);

        when(mockCommentValidationService.isValid(any(CommentCreateBindingModel.class)))
                .thenReturn(true);

        when(mockPostValidationService.isValid(any(Post.class)))
                .thenReturn(true);

        when(mockUserValidationService.isValid(any())).thenReturn(true);

        when(mockUserRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new User()));

        when(mockPostRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new Post()));

        // Act
        commentService.createComment(commentCreateBindingModel);

        // Assert
        verify(mockCommentRepository).save(any());
        verifyNoMoreInteractions(mockCommentRepository);
    }

    @Test(expected = Exception.class)
    public void createComment_whenCommentCreateBindingModelIsNotValid_throwException() throws Exception {
        // Arrange
        CommentCreateBindingModel commentCreateBindingModel = CommentsUtils.getCommentCreateBindingModel(1).get(0);

        when(mockCommentValidationService.isValid(any(CommentCreateBindingModel.class)))
                .thenReturn(false);

        when(mockPostValidationService.isValid(any(Post.class)))
                .thenReturn(true);

        when(mockUserValidationService.isValid(any())).thenReturn(true);

        when(mockUserRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new User()));

        when(mockPostRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new Post()));

        // Act
        commentService.createComment(commentCreateBindingModel);

        // Assert
        verify(mockCommentRepository).save(any());
        verifyNoMoreInteractions(mockCommentRepository);
    }

    @Test(expected = Exception.class)
    public void createComment_whenUsersAreNotValid_throwException() throws Exception {
        CommentCreateBindingModel commentCreateBindingModel = CommentsUtils.getCommentCreateBindingModel(1).get(0);

        when(mockCommentValidationService.isValid(any(CommentCreateBindingModel.class)))
                .thenReturn(true);

        when(mockPostValidationService.isValid(any(Post.class)))
                .thenReturn(true);

        when(mockUserValidationService.isValid(any())).thenReturn(false);

        when(mockUserRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new User()));

        when(mockPostRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new Post()));

        // Act
        commentService.createComment(commentCreateBindingModel);

        // Assert
        verify(mockCommentRepository).save(any());
        verifyNoMoreInteractions(mockCommentRepository);
    }

    @Test(expected = Exception.class)
    public void createComment_whenPostIsNotValid_throwException() throws Exception {
        // Arrange
        CommentCreateBindingModel commentCreateBindingModel = CommentsUtils.getCommentCreateBindingModel(1).get(0);

        when(mockCommentValidationService.isValid(any(CommentCreateBindingModel.class)))
                .thenReturn(true);

        when(mockPostValidationService.isValid(any(Post.class)))
                .thenReturn(false);

        when(mockUserValidationService.isValid(any())).thenReturn(true);

        when(mockUserRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new User()));

        when(mockPostRepository.findById(any()))
                .thenReturn(null);

        // Act
        commentService.createComment(commentCreateBindingModel);

        // Assert
        verify(mockCommentRepository).save(any());
        verifyNoMoreInteractions(mockCommentRepository);
    }

    @Test(expected = Exception.class)
    public void createComment_whenUsersAndPostAndCommentCreateBindingModelAreNotValid_throwException() throws Exception {
        // Arrange
        CommentCreateBindingModel commentCreateBindingModel = CommentsUtils.getCommentCreateBindingModel(1).get(0);

        when(mockCommentValidationService.isValid(any(CommentCreateBindingModel.class)))
                .thenReturn(false);

        when(mockPostValidationService.isValid(any(Post.class)))
                .thenReturn(false);

        when(mockUserValidationService.isValid(any())).thenReturn(false);

        when(mockUserRepository.findById(any()))
                .thenReturn(null);

        when(mockPostRepository.findById(any()))
                .thenReturn(null);

        // Act
        commentService.createComment(commentCreateBindingModel);

        // Assert
        verify(mockCommentRepository).save(any());
        verifyNoMoreInteractions(mockCommentRepository);
    }

    @Test
    public void deleteComment_whenUserAndCommentAreValid_deleteComment() throws Exception {
        // Arrange
        User user = UsersUtils.createUser();
        Post post = PostsUtils.createPost(user, user);
        Comment comment = CommentsUtils.createComment(user, user, post);

        when(mockUserRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));

        when(mockCommentValidationService.isValid(any(Comment.class)))
                .thenReturn(true);

        when(mockCommentRepository.findById(any()))
                .thenReturn(java.util.Optional.of(comment));

        when(mockUserValidationService.isValid(any()))
                .thenReturn(true);

//         Act
        commentService.deleteComment("1", "1");

        // Assert
        verify(mockCommentRepository).delete(any());
    }

    @Test(expected = Exception.class)
    public void deleteComment_whenUserIsNotValid_throwException() throws Exception {
        // Arrange
        User user = UsersUtils.createUser();
        Post post = PostsUtils.createPost(user, user);
        Comment comment = CommentsUtils.createComment(user, user, post);

        when(mockUserRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));

        when(mockUserValidationService.isValid(any()))
                .thenReturn(false);

        when(mockCommentValidationService.isValid(any(Comment.class)))
                .thenReturn(true);

        when(mockCommentRepository.findById(any()))
                .thenReturn(java.util.Optional.of(comment));

//         Act
        commentService.deleteComment("1", "1");

        // Assert
        verify(mockCommentRepository).delete(any());
    }

    @Test(expected = Exception.class)
    public void deleteComment_whenCommentIsNotValid_throwException() throws Exception {
        // Arrange
        User user = UsersUtils.createUser();
        Post post = PostsUtils.createPost(user, user);
        Comment comment = CommentsUtils.createComment(user, user, post);

        when(mockUserRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));

        when(mockUserValidationService.isValid(any()))
                .thenReturn(true);

        when(mockCommentRepository.findById(any()))
                .thenReturn(java.util.Optional.of(comment));

        when(mockCommentValidationService.isValid(any(Comment.class)))
                .thenReturn(false);

//         Act
        commentService.deleteComment("1", "1");

        // Assert
        verify(mockCommentRepository).delete(any());
    }


    @Test(expected = Exception.class)
    public void deleteComment_whenUserAndCommentAreNotValid_throwException() throws Exception {
        // Arrange
        User user = UsersUtils.createUser();
        Post post = PostsUtils.createPost(user, user);
        Comment comment = CommentsUtils.createComment(user, user, post);

        when(mockUserRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));

        when(mockUserValidationService.isValid(any()))
                .thenReturn(false);

        when(mockCommentRepository.findById(any()))
                .thenReturn(java.util.Optional.of(comment));

        when(mockCommentValidationService.isValid(any(Comment.class)))
                .thenReturn(false);

//         Act
        commentService.deleteComment("1", "1");

        // Assert
        verify(mockCommentRepository).delete(any());
    }

    @Test(expected = CustomException.class)
    public void deleteComment_whenUserIsNotAuthorized_throwException() throws Exception {
        // Arrange
        List<User> users = UsersUtils.getUsers(2);
        Post post = PostsUtils.createPost(users.get(1), users.get(1));
        Comment comment = CommentsUtils.createComment(users.get(1), users.get(1), post);


        when(mockUserRepository.findById(any()))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserValidationService.isValid(any()))
                .thenReturn(true);

        when(mockCommentRepository.findById(any()))
                .thenReturn(java.util.Optional.of(comment));

        when(mockCommentValidationService.isValid(any(Comment.class)))
                .thenReturn(true);

        // Act
        commentService.deleteComment("5", "1");

        // Assert
        verify(mockPostRepository).delete(any());
    }
}
