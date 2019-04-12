package kl.socialnetwork.repositories;

import kl.socialnetwork.domain.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    @Query(value = "" +
            "SELECT m FROM Message AS m " +
            "WHERE ((m.fromUser.id = :firstUserId AND m.toUser.id = :secondUserId) " +
            "OR  (m.fromUser.id = :secondUserId AND m.toUser.id = :firstUserId)) " +
            "ORDER BY m.time")
    List<Message> findAllMessagesBetweenTwoUsers(String firstUserId, String secondUserId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Message as m " +
            "SET m.status = 1 " +
            "WHERE m.toUser.id =:toUserId AND m.fromUser.id =:fromUserId " +
            "    AND m.status = 0")
    void updateStatusFromReadMessages(String toUserId, String fromUserId);


//    @Query(value = "select * " +
//            "from messages AS m " +
//            "         INNER JOIN " +
//            "    (select m.from_user_id as from_user_m1, max(m.time) as time_m1, count(*) as count " +
//            "    from messages AS m " +
//            "    where m.status = 0 " +
//            "      and m.to_user_id =:userId " +
//            "    GROUP BY m.from_user_id) as m1 " +
//            "                   ON m.from_user_id = m1.from_user_m1 and m.time = m1.time_m1 " +
//            "where m.status = 0 " +
//            "  and m.to_user_id =:userId " +
//            "ORDER BY m.time DESC;", nativeQuery = true)
//    List<Message> getAllUnreadMessages(@Param("userId") String loggedInUserId);

    @Query(value = "select * " +
            "from messages AS m " +
            "         INNER JOIN " +
            "    (select m.from_user_id as from_user_m1, max(m.time) as time_m1, count(*) as count " +
            "    from messages AS m " +
            "    where m.to_user_id =:userId " +
            "    GROUP BY m.from_user_id) as m1 " +
            "                   ON m.from_user_id = m1.from_user_m1 and m.time = m1.time_m1 " +
            "where m.to_user_id =:userId " +
            "ORDER BY m.time DESC;", nativeQuery = true)
    List<Message> getAllUnreadMessages(@Param("userId") String loggedInUserId);


    @Query(value = "select m.from_user_id, count(*)as count " +
            "from messages AS m " +
            "where m.status = 0 " +
            "  and m.to_user_id =:userId " +
            "GROUP BY m.from_user_id " +
            "ORDER BY m.time DESC;", nativeQuery = true)
    List<Object[]> getCountOfUnreadMessagesByFromUser(@Param("userId") String loggedInUserId);


//    @Query(value = "SELECT * " +
//            "FROM messages AS m, " +
//            "(SELECT m.from_user_id as from_user_m1, max(m.time) AS time_m1, count(*) as count " +
//            "    FROM messages AS m " +
//            "    WHERE m.status = 0 AND m.to_user_id =:userId " +
//            "    GROUP BY m.from_user_id) AS m1 " +
//            "WHERE m.status = 0 AND m.to_user_id =:userId " +
//            "   AND m.from_user_id = m1.from_user_m1 AND m.time = m1.time_m1", nativeQuery = true)
//    List<Object[]> getAllUnreadMessages(@Param("userId") String loggedInUserId);


}
