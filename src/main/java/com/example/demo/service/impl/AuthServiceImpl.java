package com.example.demo.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.AuthService;

/**
 * {@link AuthService} の DB アクセス版実装。
 * <p>
 * {@link UsersRepository} を用いてメールアドレス・パスワードで社員を照合する。<br>
 * <b>DB（users テーブル）が必要。</b>DB 未構築の間は {@link AuthServiceMock}（{@code @Primary}）が
 * 優先されるため、本実装は使用されない。DB 構築後にモックを外すと本実装が有効になる。
 * </p>
 *
 * @author 統合担当
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UsersRepository usersRepository;

    /**
     * コンストラクタインジェクション。
     *
     * @param usersRepository 社員リポジトリ
     */
    public AuthServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Users> authenticate(String email, String password) {
        return usersRepository.findByEmailAndPassword(email, password);
    }
}
