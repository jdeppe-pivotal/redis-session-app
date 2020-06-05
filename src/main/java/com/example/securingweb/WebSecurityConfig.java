package com.example.securingweb;

import java.util.ArrayList;
import java.util.List;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.resource.DirContextDnsResolver;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements EnvironmentAware {

  private List<Pair<String, Integer>> hostsAndPorts = new ArrayList<>();

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/login")
        .permitAll()
        .and()
        .logout()
        .permitAll();
  }


  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("user")
        .password("password") // Spring Security 5 requires specifying the password storage format
        .roles("USER");
  }

  @Bean
  public ConfigureRedisAction doNothingRedisAction() {
    return ConfigureRedisAction.NO_OP;
  }

  @Bean
  public ClientResources clientResources() {
    ClientResources resources = DefaultClientResources.builder()
        .socketAddressResolver(
            new HackySocketAddressResolver(new DirContextDnsResolver(), hostsAndPorts))
        .build();

    return resources;
  }

  @Override
  public void setEnvironment(Environment environment) {
    String hostsPortsParam = environment.getProperty("redis-hosts");
    String[] parts = hostsPortsParam.split(";");

    for (String hostPort : parts) {
      String[] addr = hostPort.split(":");
      hostsAndPorts.add(Pair.of(addr[0], Integer.parseInt(addr[1])));
    }
  }
}
