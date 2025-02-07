package kr.co.onehunnit.onhunnit.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.notification.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Slice<Notification> findAllByReceiver(Account receiver, Pageable pageable);

}
