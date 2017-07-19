package cats
package mtl
package hierarchy

object BaseHierarchy extends BH0 {

  implicit final def raiseFromEmpty[F[_]](implicit empty: FunctorEmpty[F]): FunctorRaise[F, Unit] = {
    new FunctorRaise[F, Unit] {
      val functor: Functor[F] = empty.functor

      def raise[A](e: Unit): F[A] = empty.empty[A]
    }
  }

}

private[hierarchy] trait BH0 {
  implicit final def askFromLocal[F[_], E](implicit local: ApplicativeLocal[F, E]): ApplicativeAsk[F, E] = local.ask

  implicit final def tellFromListen[F[_], L](implicit listen: FunctorListen[F, L]): FunctorTell[F, L] = listen.tell

  implicit final def tellFromState[F[_], L](implicit state: MonadState[F, L]): FunctorTell[F, L] = {
    new FunctorTell[F, L] {
      override val functor: Functor[F] = state.monadInstance

      override def tell(l: L): F[Unit] = state.set(l)

      override def writer[A](a: A, l: L): F[A] = functor.as(state.set(l), a)

      override def tuple[A](ta: (L, A)): F[A] = writer(ta._2, ta._1)
    }
  }
}

